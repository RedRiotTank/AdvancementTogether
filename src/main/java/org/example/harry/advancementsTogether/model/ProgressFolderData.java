package org.example.harry.advancementsTogether.model;

import java.io.File;
import java.util.logging.Logger;

public class ProgressFolderData {

    public static void createProgressFolder(File pluginFolder, Logger logger) {
        // Ensure the plugin folder exists
        if (!pluginFolder.exists()) {
            if (pluginFolder.mkdirs()) {
                logger.info("Plugin folder created successfully.");
            } else {
                logger.warning("Failed to create plugin folder.");
            }
        }

        // Create the 'progress' folder inside the plugin folder
        File progressFolder = new File(pluginFolder, "progress");
        if (!progressFolder.exists()) {
            if (progressFolder.mkdirs()) {
                logger.info("'progress' folder created successfully.");
            } else {
                logger.warning("Failed to create 'progress' folder.");
            }
        }
    }
}