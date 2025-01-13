package org.example.harry.advancementsTogether.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.config.MainConfigManager;
import org.example.harry.advancementsTogether.managers.BiomeManager;
import org.example.harry.advancementsTogether.managers.MobsManager;
import org.example.harry.advancementsTogether.utils.MessageUtils;

import java.util.Iterator;
import java.util.Set;

public class MobsListeners implements Listener {

    private Main plugin;
    private final MobsManager mobsManager;

    public MobsListeners(Main plugin, MobsManager mobsManager) {
        this.plugin = plugin;
        this.mobsManager = mobsManager;
    }

    private final Set<EntityType> mobsEspecificos = Set.of(
            EntityType.SLIME, EntityType.GHAST,
            EntityType.MAGMA_CUBE, EntityType.SHULKER,
            EntityType.HOGLIN, EntityType.PHANTOM,
            EntityType.ENDER_DRAGON
    );

    @EventHandler
    public void onHostileMobDeath(EntityDeathEvent event) {

        MainConfigManager mainConfigManager = plugin.getMainConfigManager();
        EntityType mobType = event.getEntityType();
        String entityType = String.valueOf(event.getEntityType()).replace("_", " ");
        Player killer = event.getEntity().getKiller();

        String overworld = mainConfigManager.getConfigOverworld();
        String nether = mainConfigManager.getConfigNether();
        String theEnd = mainConfigManager.getConfigEnd();

        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerWorld = p.getWorld().getName();

            if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                return;
            }
        }

        if (event.getEntity() instanceof Monster || mobsEspecificos.contains(mobType) && mobType != EntityType.WARDEN) {
            if (killer != null) {
                if (!mobsManager.isMobFound(mobType)) {
                    mobsManager.addMob(mobType);
                    if (mainConfigManager.isMessagesFoundMobsBoolean()){
                        Bukkit.broadcastMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesFoundMobs()
                                .replace("%player%", killer.getName()).replace("%server_name%", Main.prefix)
                                .replace("%mob%", entityType).replace("%world%", killer.getWorld().getName())));
                    }
                }
            }
        }
    }
}
