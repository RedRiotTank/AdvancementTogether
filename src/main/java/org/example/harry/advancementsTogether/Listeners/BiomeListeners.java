package org.example.harry.advancementsTogether.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.config.MainConfigManager;
import org.example.harry.advancementsTogether.managers.BiomeManager;
import org.example.harry.advancementsTogether.utils.MessageUtils;

import java.util.Optional;

public class BiomeListeners implements Listener {

    private final BiomeManager biomeManager;
    private final MainConfigManager mainConfigManager;
    private final String overworld;
    private final String nether;
    private final String theEnd;
    private final Advancement adventuringTimeAdvancement;

    public BiomeListeners(Main plugin, BiomeManager biomeManager) {
        this.biomeManager = biomeManager;
        this.mainConfigManager = plugin.getMainConfigManager();

        this.overworld = mainConfigManager.getConfigOverworld();
        this.nether = mainConfigManager.getConfigNether();
        this.theEnd = mainConfigManager.getConfigEnd();

        this.adventuringTimeAdvancement = Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("adventure/adventuring_time"));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isAnyPlayerInValidWorld()) {
            return;
        }

        if (biomeManager.getBiomesCount() >= 53) {
            grantAdvancementToAll();
        }

        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase(overworld)) {
            detectNewBiome(player);
        }
    }

    private boolean isAnyPlayerInValidWorld() {
        return Bukkit.getOnlinePlayers().stream()
                .anyMatch(player -> {
                    String world = player.getWorld().getName();
                    return world.equals(overworld) || world.equals(nether) || world.equals(theEnd);
                });
    }

    private void grantAdvancementToAll() {
        Optional.ofNullable(adventuringTimeAdvancement).ifPresent(advancement -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                if (!progress.isDone()) {
                    progress.getRemainingCriteria().forEach(progress::awardCriteria);
                }
            }
        });
    }

    private void detectNewBiome(Player player) {
        Biome biome = player.getLocation().getBlock().getBiome();

        if (!biomeManager.isBiomeFound(biome)) {
            biomeManager.addBiome(biome);

            if (mainConfigManager.isMessagesFoundBiomesBoolean()) {
                String biomeString = biome.name().replace("_", " ");
                String message = mainConfigManager.getMessagesFoundBiomes()
                        .replace("%biome%", biomeString)
                        .replace("%player%", player.getName())
                        .replace("%server_name%", Main.prefix)
                        .replace("%world%", player.getWorld().getName());

                Bukkit.broadcastMessage(MessageUtils.getColoredMessage(Main.prefix + message));
            }
        }
    }
}
