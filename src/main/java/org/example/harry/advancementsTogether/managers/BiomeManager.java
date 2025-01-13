package org.example.harry.advancementsTogether.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
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

public class BiomeManager {

    private final Main plugin;
    private final Set<Biome> biomesFound;
    private final File biomeFile;
    private FileConfiguration biomeConfig;
    private long lastModifiedTime;
    private int Counter;

    public BiomeManager(Main plugin) {
        this.plugin = plugin;
        this.biomesFound = new CopyOnWriteArraySet<>();

        File progressFolder = new File(plugin.getDataFolder(), "progress");
        this.biomeFile = new File(progressFolder, "adventure_time_Progress.yml");

        if (!biomeFile.exists()) {
            createDefaultBiomeFile();
        }

        loadBiomes();
    }

    private synchronized void createDefaultBiomeFile() {
        try (InputStream inputStream = plugin.getResource("adventure_time_Progress.yml")) {
            if (inputStream != null) {
                Files.copy(inputStream, biomeFile.toPath());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error to copy adventure_time_Progress.yml: " + e.getMessage());
        }
    }

    public synchronized void loadBiomes() {

        if (!biomeFile.exists()) {
            try {
                if (biomeFile.createNewFile()) {
                    plugin.getLogger().info("File adventure_time_Progress.yml successfully created.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error creating file adventure_time_Progress.yml: " + e.getMessage());
            }
        }

        biomeConfig = YamlConfiguration.loadConfiguration(biomeFile);
        lastModifiedTime = biomeFile.lastModified();

        biomesFound.clear();
        if (biomeConfig.contains("biomesFound")) {
            List<String> biomasList = biomeConfig.getStringList("biomesFound");
            for (String biomeName : biomasList) {
                try {
                    Biome biome = Biome.valueOf(biomeName);
                    biomesFound.add(biome);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("The Biome" + biomeName + " is not valid and will not count.");
                }
            }
        }

        if (biomeConfig.contains("Counter")) {
            Counter = biomeConfig.getInt("Counter");
            plugin.getLogger().info("Loading counter [Biomes]: " + Counter);
        } else {
            Counter = biomesFound.size();
            plugin.getLogger().info("Initial counter with: " + Counter);
        }
        saveBiomes();
    }

    public synchronized void saveBiomes() {
        List<String> biomasStrings = new ArrayList<>(biomesFound.size());
        for (Biome biome : biomesFound) {
            biomasStrings.add(biome.toString());
        }

        biomeConfig.set("biomesFound", biomasStrings);
        biomeConfig.set("Counter", Counter);

        try {
            biomeConfig.save(biomeFile);
            lastModifiedTime = biomeFile.lastModified();
            plugin.getLogger().info("Saving [List] " + biomesFound.size() + " biomes.");
        } catch (IOException e) {
            plugin.getLogger().warning("File could not be saved biomesProgress.yml: " + e.getMessage());
        }
    }

    public synchronized void reloadBiomes() {
        loadBiomes();
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(Main.prefix+"&eList of biomes reloaded."));
    }

    public boolean isBiomeFound(Biome biome) {
        return biomesFound.contains(biome);
    }

    public synchronized void addBiome(Biome biome) {
        if (biomesFound.add(biome)) {
            Counter++;
            saveBiomes();
        }
    }

    public Set<Biome> getBiomasEncontrados() {
        return new HashSet<>(biomesFound);
    }

    public int getBiomesCount() {
        return Counter;
    }

    public void getBiomesList(CommandSender sender) {
        if (biomesFound.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No biomes found yet.");
        } else {
            StringBuilder message = new StringBuilder(ChatColor.WHITE + "- ");
            for (Biome biome : biomesFound) {
                message.append(ChatColor.AQUA)
                        .append(biome.toString().replace("_", " "))
                        .append(ChatColor.GRAY).append(" | ");
            }
            if (message.length() > 0) {
                message.setLength(message.length() - 3);
            }
            sender.sendMessage(message.toString());
        }
    }
}