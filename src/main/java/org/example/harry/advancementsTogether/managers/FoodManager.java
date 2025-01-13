package org.example.harry.advancementsTogether.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class FoodManager {

    private final Main plugin;
    private final Set<Material> itemsConsumed;
    private final File progressFile;
    private FileConfiguration progressConfig;
    private long lastModifiedTime;
    private int Counter;

    public FoodManager(Main plugin) {
        this.plugin = plugin;
        this.itemsConsumed = new CopyOnWriteArraySet<>();

        File progressFolder = new File(plugin.getDataFolder(), "progress");
        if (!progressFolder.exists()) {
            progressFolder.mkdirs();
        }

        this.progressFile = new File(progressFolder, "balanced_diet_Progress.yml");

        if (!progressFile.exists()) {
            createDefaultProgressFile();
        }

        loadItems();
    }

    private void createDefaultProgressFile() {
        try (InputStream inputStream = plugin.getResource("balanced_diet_Progress.yml")) {
            if (inputStream != null) {
                Files.copy(inputStream, progressFile.toPath());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error copying balanced_diet_Progress.yml: " + e.getMessage());
        }
    }

    public synchronized void loadItems() {
        if (!progressFile.exists()) {
            try {
                if (progressFile.createNewFile()) {
                    plugin.getLogger().info("File balanced_diet_Progress.yml successfully created.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error creating file balanced_diet_Progress.yml: " + e.getMessage());
            }
        }

        progressConfig = YamlConfiguration.loadConfiguration(progressFile);
        lastModifiedTime = progressFile.lastModified();

        itemsConsumed.clear();
        if (progressConfig.contains("ItemsConsumed")) {
            List<String> itemsList = progressConfig.getStringList("ItemsConsumed");
            for (String itemName : itemsList) {
                try {
                    Material item = Material.valueOf(itemName);
                    itemsConsumed.add(item);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("The item " + itemName + " is not valid and will not count.");
                }
            }
        }

        if (progressConfig.contains("Counter")) {
            Counter = progressConfig.getInt("Counter");
            plugin.getLogger().info("Loading counter [Items]: " + Counter);
        } else {
            Counter = itemsConsumed.size();
            plugin.getLogger().info("Initial counter with: " + Counter);
        }
        saveItems();
    }

    public synchronized void saveItems() {
        List<String> itemsStrings = new ArrayList<>(itemsConsumed.size());
        for (Material item : itemsConsumed) {
            itemsStrings.add(item.toString());
        }

        progressConfig.set("ItemsConsumed", itemsStrings);
        progressConfig.set("Counter", Counter);

        try {
            progressConfig.save(progressFile);
            lastModifiedTime = progressFile.lastModified();
            plugin.getLogger().info("Saving [List] " + itemsConsumed.size() + " consumed items.");
        } catch (IOException e) {
            plugin.getLogger().warning("File could not be saved balanced_diet_Progress.yml: " + e.getMessage());
        }
    }

    public synchronized void reloadItems() {
        loadItems();
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "List of consumed items reloaded.");
    }

    public boolean isItemConsumed(Material item) {
        return itemsConsumed.contains(item);
    }

    public synchronized void addItem(Material item) {
        if (itemsConsumed.add(item)) {
            Counter++;
            saveItems();
        }
    }

    public Set<Material> getItemsConsumed() {
        return new HashSet<>(itemsConsumed);
    }

    public int getItemCount() {
        return Counter;
    }

    public void getItemsList(CommandSender sender) {
        if (itemsConsumed.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No items found yet.");
        } else {
            StringBuilder message = new StringBuilder(ChatColor.WHITE + "- ");
            for (Material item : itemsConsumed) {
                message.append(ChatColor.GOLD)
                        .append(item.toString().replace("_", " "))
                        .append(ChatColor.GRAY).append(" | ");
            }
            if (message.length() > 0) {
                message.setLength(message.length() - 3);
            }
            sender.sendMessage(message.toString());
        }
    }
}

