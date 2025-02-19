package org.example.harry.advancementsTogether.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.config.MainConfigManager;
import org.example.harry.advancementsTogether.managers.MobsManager;
import org.example.harry.advancementsTogether.utils.MessageUtils;

import java.util.Set;

public class MobsListeners implements Listener {

    private final MobsManager mobsManager;
    private final MainConfigManager mainConfigManager;

    private static final Set<EntityType> MOBS_ESPECIFICOS = Set.of(
            EntityType.SLIME, EntityType.GHAST,
            EntityType.MAGMA_CUBE, EntityType.SHULKER,
            EntityType.HOGLIN, EntityType.PHANTOM,
            EntityType.ENDER_DRAGON
    );

    public MobsListeners(MainConfigManager mainConfigManager, MobsManager mobsManager) {
        this.mainConfigManager = mainConfigManager;
        this.mobsManager = mobsManager;
    }

    @EventHandler
    public void onHostileMobDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        EntityType mobType = event.getEntityType();

        if (killer == null || !isAnyPlayerInValidWorld()) {
            return;
        }

        if (event.getEntity() instanceof Monster || MOBS_ESPECIFICOS.contains(mobType)) {
            processMobDeath(killer, mobType);
        }
    }

    private boolean isAnyPlayerInValidWorld() {
        return Bukkit.getOnlinePlayers().stream()
                .map(player -> player.getWorld().getName())
                .anyMatch(world -> world.equals(mainConfigManager.getConfigOverworld()) ||
                        world.equals(mainConfigManager.getConfigNether()) ||
                        world.equals(mainConfigManager.getConfigEnd()));
    }

    private void processMobDeath(Player killer, EntityType mobType) {
        if (!mobsManager.isMobFound(mobType)) {
            mobsManager.addMob(mobType);

            if (mainConfigManager.isMessagesFoundMobsBoolean()) {
                String message = String.format(
                        "%s%s",
                        Main.prefix,
                        mainConfigManager.getMessagesFoundMobs()
                                .replace("%player%", killer.getName())
                                .replace("%server_name%", Main.prefix)
                                .replace("%mob%", mobType.name().replace("_", " "))
                                .replace("%world%", killer.getWorld().getName())
                );

                Bukkit.broadcastMessage(MessageUtils.getColoredMessage(message));
            }
        }
    }
}
