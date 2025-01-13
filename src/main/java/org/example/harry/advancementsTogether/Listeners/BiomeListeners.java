package org.example.harry.advancementsTogether.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.config.MainConfigManager;
import org.example.harry.advancementsTogether.managers.BiomeManager;
import org.example.harry.advancementsTogether.utils.MessageUtils;

import java.util.Iterator;
import java.util.Set;

public class BiomeListeners implements Listener {

    private Main plugin;
    private BiomeManager biomeManager;

    public BiomeListeners(Main plugin, BiomeManager biomeManager){
        this.plugin = plugin;
        this.biomeManager = biomeManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("adventure/adventuring_time"));
        MainConfigManager mainConfigManager = plugin.getMainConfigManager();

        String overworld = mainConfigManager.getConfigOverworld();
        String nether = mainConfigManager.getConfigNether();
        String theEnd = mainConfigManager.getConfigEnd();

        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerWorld = p.getWorld().getName();

            if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                return;
            }
        }

        if (biomeManager.getBiomesCount() >= 53){
            if (advancement != null){
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                if (!progress.isDone()) {
                    Iterator<String> criteria = progress.getRemainingCriteria().iterator();
                    while (criteria.hasNext()) {
                        progress.awardCriteria(criteria.next());
                    }
                }
            }
        }

        if (player.getWorld().getName().equalsIgnoreCase(overworld)) {
            Biome biome = player.getLocation().getBlock().getBiome();
            String biomeString = String.valueOf(biome).replace("_", " ");

            if (!biomeManager.isBiomeFound(biome)) {
                biomeManager.addBiome(biome);
                if (mainConfigManager.isMessagesFoundBiomesBoolean()){
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesFoundBiomes()
                            .replace("%biome%", biomeString).replace("%player%", player.getName())
                            .replace("%server_name%", Main.prefix).replace("%world%", player.getWorld().getName())));
                }
            }
        }
    }
}
