package org.example.harry.advancementsTogether.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
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

public class MobsManager {

    private final Main plugin;
    private final Set<EntityType> mobsEncontrados;
    private final File mobFile;
    private FileConfiguration mobConfig;
    private long lastModifiedTime;
    private int count;

    public MobsManager(Main plugin) {
        this.plugin = plugin;
        this.mobsEncontrados = new CopyOnWriteArraySet<>();

        File progressFolder = new File(plugin.getDataFolder(), "progress");

        this.mobFile = new File(progressFolder, "monsters_hunted.yml");

        if (!mobFile.exists()) {
            createDefaultMobFile();
        }

        loadMobs();
    }

    private void createDefaultMobFile() {
        try (InputStream inputStream = plugin.getResource("monsters_hunted.yml")) {
            if (inputStream != null) {
                Files.copy(inputStream, mobFile.toPath());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error to copy monsters_hunted.yml: " + e.getMessage());
        }
    }

    public synchronized void loadMobs() {

        if (!mobFile.exists()) {
            try {
                if (mobFile.createNewFile()) {
                    plugin.getLogger().info("File monsters_hunted.yml successfully created.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error creating file monsters_hunted.yml: " + e.getMessage());
            }
        }

        mobConfig = YamlConfiguration.loadConfiguration(mobFile);
        lastModifiedTime = mobFile.lastModified();

        mobsEncontrados.clear();
        if (mobConfig.contains("MobsFound")) {
            List<String> mobsList = mobConfig.getStringList("MobsFound");
            for (String mobName : mobsList) {
                try {
                    EntityType mob = EntityType.valueOf(mobName);
                    mobsEncontrados.add(mob);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("The mob " + mobName + " is not valid and will not count.");
                }
            }
        }

        if (mobConfig.contains("Counter")) {
            count = mobConfig.getInt("Counter");
            plugin.getLogger().info("Loading counter [Mobs]: " + count);
        } else {
            count = mobsEncontrados.size();
            plugin.getLogger().info("Initial counter with: " + count);
        }
        saveMobs();
    }

    public synchronized void saveMobs() {
        List<String> mobsStrings = new ArrayList<>(mobsEncontrados.size());
        for (EntityType mob : mobsEncontrados) {
            mobsStrings.add(mob.toString());
        }

        mobConfig.set("MobsFound", mobsStrings);
        mobConfig.set("Counter", count);

        try {
            mobConfig.save(mobFile);
            lastModifiedTime = mobFile.lastModified();
            plugin.getLogger().info("Saving [List] " + mobsEncontrados.size() + " mobs.");
        } catch (IOException e) {
            plugin.getLogger().warning("File could not be saved mobProgress.yml: " + e.getMessage());
        }
    }

    public synchronized void reloadMobs() {
        loadMobs();
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(Main.prefix+"&eList of mobs reloaded."));
    }

    public boolean isMobFound(EntityType mob) {
        return mobsEncontrados.contains(mob);
    }

    public synchronized void addMob(EntityType mob) {
        if (mobsEncontrados.add(mob)) {
            count++;
            saveMobs();
        }
    }

    public Set<EntityType> getMobsEncontrados() {
        return new HashSet<>(mobsEncontrados);
    }

    public int getMobsCount() {
        return count;
    }

    public void getMobsList(CommandSender sender) {
        if (mobsEncontrados.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No mobs found yet.");
        } else {
            StringBuilder message = new StringBuilder(ChatColor.WHITE + "- ");
            for (EntityType mob : mobsEncontrados) {
                message.append(ChatColor.DARK_PURPLE)
                        .append(mob.toString().replace("_", " "))
                        .append(ChatColor.GRAY).append(" | ");
            }
            if (message.length() > 0) {
                message.setLength(message.length() - 3);
            }
            sender.sendMessage(message.toString());
        }
    }
}

