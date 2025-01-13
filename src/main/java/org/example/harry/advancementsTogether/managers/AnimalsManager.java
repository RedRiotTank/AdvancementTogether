package org.example.harry.advancementsTogether.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AnimalsManager {

    private final Main plugin;
    private final Set<EntityType> AnimalsBreeding;
    private final File animalFile;
    private FileConfiguration animalConfig;
    private long lastModifiedTime;
    private int count;

    public AnimalsManager(Main plugin) {
        this.plugin = plugin;
        this.AnimalsBreeding = new CopyOnWriteArraySet<>();

        File progressFolder = new File(plugin.getDataFolder(), "progress");

        this.animalFile = new File(progressFolder, "two_by_two_Progress.yml");

        if (!animalFile.exists()) {
            createDefaultAnimalFile();
        }

        loadAnimals();
    }

    private void createDefaultAnimalFile() {
        try (InputStream inputStream = plugin.getResource("two_by_two_Progress.yml")) {
            if (inputStream != null) {
                Files.copy(inputStream, animalFile.toPath());
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error to copy two_by_two_Progress.yml: " + e.getMessage());
        }
    }

    public synchronized void loadAnimals() {

        if (!animalFile.exists()) {
            try {
                if (animalFile.createNewFile()) {
                    plugin.getLogger().info("File two_by_two_Progress.yml successfully created.");
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Error creating file two_by_two_Progress.yml: " + e.getMessage());
            }
        }

        animalConfig = YamlConfiguration.loadConfiguration(animalFile);
        lastModifiedTime = animalFile.lastModified();

        AnimalsBreeding.clear();
        if (animalConfig.contains("AnimalsReproduced")) {
            List<String> animalsList = animalConfig.getStringList("AnimalsReproduced");
            for (String animalName : animalsList) {
                try {
                    EntityType animal = EntityType.valueOf(animalName);
                    AnimalsBreeding.add(animal);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("The Animal " + animalName + " is not valid and will not count.");
                }
            }
        }

        if (animalConfig.contains("Counter")) {
            count = animalConfig.getInt("Counter");
            plugin.getLogger().info("Loading counter [Animals]: " + count);
        } else {
            count = AnimalsBreeding.size();
            plugin.getLogger().info("Initial counter with: " + count);
        }
        saveAnimals();
    }

    public synchronized void saveAnimals() {
        List<String> animalsStrings = new ArrayList<>(AnimalsBreeding.size());
        for (EntityType animal : AnimalsBreeding) {
            animalsStrings.add(animal.toString());
        }

        animalConfig.set("AnimalsReproduced", animalsStrings);
        animalConfig.set("Counter", count);

        try {
            animalConfig.save(animalFile);
            lastModifiedTime = animalFile.lastModified();
            plugin.getLogger().info("Saving [List] " + AnimalsBreeding.size() + " reproduced animals.");
        } catch (IOException e) {
            plugin.getLogger().warning("File could not be saved animalsProgress.yml: " + e.getMessage());
        }
    }

    public synchronized void reloadAnimals() {
        loadAnimals();
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(Main.prefix+"&eList of animals reloaded."));
    }

    public boolean isAnimalReproduced(EntityType animal) {
        return AnimalsBreeding.contains(animal);
    }

    public synchronized void addAnimal(EntityType animal) {
        if (AnimalsBreeding.add(animal)) {
            count++;
            saveAnimals();
        }
    }

    public Set<EntityType> getAnimalsReproduced() {
        return new HashSet<>(AnimalsBreeding);
    }

    public int getAnimalCount() {
        return count;
    }

    public void getMobsList(CommandSender sender) {
        if (AnimalsBreeding.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No animals found yet.");
        } else {
            StringBuilder message = new StringBuilder(ChatColor.WHITE + "- ");
            for (EntityType mob : AnimalsBreeding) {
                message.append(ChatColor.GOLD)
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
