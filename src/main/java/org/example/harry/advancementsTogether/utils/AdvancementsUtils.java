package org.example.harry.advancementsTogether.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.config.MainConfigManager;

import java.util.Iterator;

public class AdvancementsUtils {

    private final Main plugin;

    public AdvancementsUtils(Main plugin) {
        this.plugin = plugin;
    }

    public void createCustomAdvancementForPlayer(Player player) {

        String playerName = player.getName();
        MainConfigManager mainConfigManager = plugin.getMainConfigManager();

        NamespacedKey key = new NamespacedKey(plugin, "welcome_achievement" + playerName.toLowerCase());

        String title = MessageUtils.getColoredMessage(mainConfigManager.getMessagesWelcomeAchivementTitle()
                .replace("%player%", playerName).replace("%server_name%",Main.prefix).replace("%world%", player.getWorld().getName()));
        String description = MessageUtils.getColoredMessage(mainConfigManager.getMessagesWelcomeAchivementDescription()
                .replace("%player%", player.getName()).replace("%server_name%",Main.prefix).replace("%world%", player.getWorld().getName()));
        String frame = mainConfigManager.getMessagesWelcomeAchivementFrame();
        String rawIcon = mainConfigManager.getMessagesWelcomeAchivementIcon();
        String rawBackground = mainConfigManager.getMessagesWelcomeAchivementBackground();

        String icon = rawIcon != null ? rawIcon.toLowerCase() : "diamond";
        if (!icon.contains(":")) {
            icon = "minecraft:" + icon;
        }

        String background = rawBackground != null ? rawBackground.toLowerCase() : "cobblestone";
        if (!background.contains(":")) {
            background = "minecraft:textures/block/" + background + ".png";
        }

        String json = """
                {
                    "display": {
                        "icon": {
                            "item": "%s",
                            "id": "%s"
                        },
                        "title": {
                            "text": "%s",
                            "color": "white"
                        },
                        "description": {
                            "text": "%s",
                            "color": "green"
                        },
                        "frame": "%s",
                        "show_toast": true,
                        "announce_to_chat": false,
                        "hidden": false,
                        "background": "%s"
                    },
                    "criteria": {
                        "impossible": {
                            "trigger": "minecraft:impossible"
                        }
                    }
                }
                """.formatted(icon, icon, title, description, frame, background);
        //.formatted(icon, icon, title2, titleColor, description, descriptionColor, frame, background);

        try {
            Bukkit.getUnsafe().loadAdvancement(key, json);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Customized achievement for " + playerName + " already exists or could not be uploaded.");
        }
    }

    public void grantAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey(plugin, "welcome_achievement" + player.getName().toLowerCase());
        Advancement advancement = Bukkit.getAdvancement(key);

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            Iterator<String> criteria = progress.getRemainingCriteria().iterator();

            while (criteria.hasNext()) {
                progress.awardCriteria(criteria.next());
            }
        }
    }

    public void revokeAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey(plugin, "welcome_achievement" + player.getName().toLowerCase());
        Advancement advancement = Bukkit.getAdvancement(key);

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            Iterator<String> criteria = progress.getAwardedCriteria().iterator();

            while (criteria.hasNext()) {
                progress.revokeCriteria(criteria.next());
            }
            Bukkit.getUnsafe().removeAdvancement(key);
        }
    }
}
