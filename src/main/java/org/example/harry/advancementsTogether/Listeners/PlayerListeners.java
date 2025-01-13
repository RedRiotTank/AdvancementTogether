package org.example.harry.advancementsTogether.Listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.config.MainConfigManager;
import org.example.harry.advancementsTogether.managers.AnimalsManager;
import org.example.harry.advancementsTogether.managers.BiomeManager;
import org.example.harry.advancementsTogether.managers.FoodManager;
import org.example.harry.advancementsTogether.managers.MobsManager;
import org.example.harry.advancementsTogether.utils.AdvancementsUtils;
import org.example.harry.advancementsTogether.utils.MessageUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PlayerListeners implements Listener {

    private Main plugin;
    private AdvancementsUtils utils;


    public PlayerListeners(Main plugin, AdvancementsUtils utils) {
        this.plugin = plugin;
        this.utils = utils;
    }

    private final Set<Material> itemsDietaBalanceada = Set.of(
            Material.APPLE, Material.BAKED_POTATO, Material.BEETROOT,
            Material.BEETROOT_SOUP, Material.BREAD, Material.CARROT,
            Material.CHORUS_FRUIT, Material.COOKED_CHICKEN, Material.COOKED_COD,
            Material.COOKED_MUTTON, Material.COOKED_PORKCHOP, Material.COOKED_RABBIT,
            Material.COOKED_SALMON, Material.COOKIE, Material.DRIED_KELP,
            Material.ENCHANTED_GOLDEN_APPLE, Material.GLOW_BERRIES, Material.GOLDEN_APPLE,
            Material.GOLDEN_CARROT, Material.HONEY_BOTTLE, Material.MELON_SLICE,
            Material.MUSHROOM_STEW, Material.POISONOUS_POTATO, Material.POTATO,
            Material.PUFFERFISH, Material.PUMPKIN_PIE, Material.RABBIT_STEW,
            Material.BEEF, Material.CHICKEN, Material.COD,
            Material.MUTTON, Material.PORKCHOP, Material.RABBIT,
            Material.SALMON, Material.ROTTEN_FLESH, Material.SPIDER_EYE,
            Material.COOKED_BEEF, Material.SUSPICIOUS_STEW, Material.SWEET_BERRIES,
            Material.TROPICAL_FISH
    );


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        MainConfigManager mainConfigManager = plugin.getMainConfigManager();

        List<String> message = mainConfigManager.getMessajesWelcomeMessage();

        if (mainConfigManager.isConfigWelcomeMessageBolean()) {
            for (String m : message) {
                player.sendMessage(MessageUtils.getColoredMessage(m.replace("%player%", player.getName()).replace("%server_name%", Main.prefix)
                        .replace("%world%", player.getWorld().getName())));
            }
        }

        if (mainConfigManager.isMessagesWelcomeAchivementBoolean()) {

            utils.createCustomAdvancementForPlayer(event.getPlayer());

            new BukkitRunnable() {
                @Override
                public void run() {
                    utils.grantAdvancement(player);
                }
            }.runTaskLater(plugin, 40L); // 40 ticks = 2 segundos
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        MainConfigManager mainConfigManager = plugin.getMainConfigManager();

        if (mainConfigManager.isMessagesWelcomeAchivementBoolean()) {
            utils.revokeAdvancement(e.getPlayer());
        }
    }


    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        MainConfigManager mainConfigManager = plugin.getMainConfigManager();

        String advancementKey = event.getAdvancement().getKey().getKey();

        String overworld = mainConfigManager.getConfigOverworld();
        String nether = mainConfigManager.getConfigNether();
        String theEnd = mainConfigManager.getConfigEnd();


        if (advancement.getDisplay() == null) {
            return;
        }

        if (advancementKey.equalsIgnoreCase("welcome_achievement" + player.getName().toLowerCase())
                || advancementKey.equalsIgnoreCase("story/root")
                || advancementKey.equalsIgnoreCase("adventure/root")
                || advancementKey.equalsIgnoreCase("husbandry/root")
                || advancementKey.equalsIgnoreCase("nether/root")) {
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerWorld = p.getWorld().getName();

            if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                return;
            }
        }


        String advancementName = advancement.getDisplay().getTitle();
        String hotbarMessage = MessageUtils.getColoredMessage(mainConfigManager.getMessageObtainActionBar()
                .replace("%player%", player.getName()).replace("%server_name%", Main.prefix)
                .replace("%advancement%", advancementName).replace("%world%", player.getWorld().getName()));
        String moa = mainConfigManager.getMessageObtainAdvancement();
        String moas = mainConfigManager.getMessageObtainAdvancementSound();
        String Moap = mainConfigManager.getMessageObtainAdvancementParticle();

        if (mainConfigManager.isMessageObtainAdvancementBoolean()) {
            player.sendMessage(MessageUtils.getColoredMessage(Main.prefix + moa
                    .replace("%player%", player.getName()).replace("%advancement%", advancementName)
                    .replace("%server_name%", Main.prefix).replace("%world%", player.getWorld().getName())));
            player.playSound(player.getLocation(), Sound.valueOf(moas), 1.0f, 1.0f);
            player.getWorld().spawnParticle(Particle.valueOf(Moap), player.getLocation(), 30, 0.5, 1, 0.5);
            player.playSound(player.getLocation(), Sound.valueOf(moas), 1.0f, 1.0f);
        }

        if (mainConfigManager.isMessageObtainActionBarBoolean()) {
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new TextComponent(hotbarMessage));
        }

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
            if (!onlinePlayer.equals(player)){
                String playerWorld = onlinePlayer.getWorld().getName();
                if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                    return;
                }
                grantAdvancement(onlinePlayer, advancement);
            }
        }
    }

    private void grantAdvancement(Player player, Advancement advancement) {
        AdvancementProgress progress = player.getAdvancementProgress(advancement);

        for (String criteria : progress.getRemainingCriteria()) {
            progress.awardCriteria(criteria);
        }
    }
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e){

        Player p = e.getPlayer();
        MainConfigManager mainConfigManager = plugin.getMainConfigManager();
        MobsManager mobsManager = plugin.getMobsManager();
        BiomeManager biomeManager = plugin.getBiomeManager();
        AnimalsManager animalsManager = plugin.getAnimalsManager();
        FoodManager foodManager = plugin.getFoodManager();

        String overworld = mainConfigManager.getConfigOverworld();
        String nether = mainConfigManager.getConfigNether();
        String theEnd = mainConfigManager.getConfigEnd();

        Advancement advancementBiomes = Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("adventure/adventuring_time"));
        Advancement advancementMobs = Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("adventure/kill_all_mobs"));
        Advancement advancementAnimals = Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("husbandry/bred_all_animals"));
        Advancement advancementFood = Bukkit.getAdvancement(org.bukkit.NamespacedKey.minecraft("husbandry/balanced_diet"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerWorld = player.getWorld().getName();

            if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                return;
            }
        }

        int mobKillCount = mobsManager.getMobsCount();
        if (mobKillCount >= 36) {
            if (advancementMobs != null){
                AdvancementProgress progress = p.getAdvancementProgress(advancementMobs);
                if (!progress.isDone()) {
                    Iterator<String> criteria = progress.getRemainingCriteria().iterator();
                    while (criteria.hasNext()) {
                        progress.awardCriteria(criteria.next());
                    }
                }
            }
        }

        if (biomeManager.getBiomesCount() >= 53){
            if (advancementBiomes != null){
                AdvancementProgress progress = p.getAdvancementProgress(advancementBiomes);
                if (!progress.isDone()) {
                    Iterator<String> criteria = progress.getRemainingCriteria().iterator();
                    while (criteria.hasNext()) {
                        progress.awardCriteria(criteria.next());
                    }
                }
            }
        }

        if (animalsManager.getAnimalCount() >= 25){
            if (advancementAnimals != null){
                AdvancementProgress progress = p.getAdvancementProgress(advancementAnimals);
                if (!progress.isDone()) {
                    Iterator<String> criteria = progress.getRemainingCriteria().iterator();
                    while (criteria.hasNext()) {
                        progress.awardCriteria(criteria.next());
                    }
                }
            }
        }

        if (foodManager.getItemCount() >= 40){
            if (advancementFood != null) {
                AdvancementProgress progress = p.getAdvancementProgress(advancementFood);
                if (!progress.isDone()) {
                    Iterator<String> criteria = progress.getRemainingCriteria().iterator();
                    while (criteria.hasNext()) {
                        progress.awardCriteria(criteria.next());
                    }
                }
            }
        }
    }

    //Events Advancement
    @EventHandler
    public void onAnimalBreed(EntityBreedEvent event) {

        AnimalsManager animalsManager = plugin.getAnimalsManager();
        MainConfigManager mainCM = plugin.getMainConfigManager();

        String overworld = mainCM.getConfigOverworld();
        String nether = mainCM.getConfigNether();
        String theEnd = mainCM.getConfigEnd();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerWorld = player.getWorld().getName();

            if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                return;
            }
        }

        if (event.getBreeder() instanceof Player) {

            Player player = (Player) event.getBreeder();

            if (event.getEntity() instanceof Animals) {

                EntityType animalType = EntityType.valueOf(String.valueOf(event.getEntity().getType()).toUpperCase().replace("_", " "));;

                if (!animalsManager.isAnimalReproduced(animalType)) {
                    animalsManager.addAnimal(animalType);
                    if (mainCM.isMessagesFoundAnimalsBoolean()) {
                        Bukkit.broadcastMessage(MessageUtils.getColoredMessage(Main.prefix + mainCM.getMessagesFoundAnimals()
                                .replace("%player%", player.getName()).replace("%server_name%",mainCM.getPrefix())
                                .replace("%animal%", animalType.name()).replace("%world%", player.getWorld().getName())));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpecialAnimalHatch(CreatureSpawnEvent event) {
        EntityType type = event.getEntityType();
        AnimalsManager animalsManager = plugin.getAnimalsManager();
        MainConfigManager mainCM = plugin.getMainConfigManager();

        String overworld = mainCM.getConfigOverworld();
        String nether = mainCM.getConfigNether();
        String theEnd = mainCM.getConfigEnd();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerWorld = player.getWorld().getName();

            if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                return;
            }
        }

        Player nearestPlayer = null;
        double minDistance = 30.0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = player.getLocation().distance(event.getEntity().getLocation());
            if (distance <= minDistance) {
                minDistance = distance;
                nearestPlayer = player;
            }
        }


        if ((type == EntityType.TURTLE || type == EntityType.SNIFFER)
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            EntityType animalType = EntityType.valueOf(String.valueOf(event.getEntity().getType()).toUpperCase().replace("_"," "));
            if (!animalsManager.isAnimalReproduced(animalType)){
                animalsManager.addAnimal(animalType);
                if (mainCM.isMessagesFoundAnimalsBoolean()) {
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage(Main.prefix + mainCM.getMessagesFoundAnimals()
                            .replace("%player%", nearestPlayer.getName()).replace("%server_name%",mainCM.getPrefix())
                            .replace("%animal%", animalType.name()).replace("%world%", nearestPlayer.getWorld().getName())));
                }
            }
        }

        if ((type == EntityType.FROG && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.METAMORPHOSIS)){
            EntityType animalType = EntityType.valueOf(String.valueOf(event.getEntity().getType()).toUpperCase().replace("_", " "));
            if (!animalsManager.isAnimalReproduced(animalType)){
                animalsManager.addAnimal(animalType);
                if (mainCM.isMessagesFoundAnimalsBoolean()) {
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage(Main.prefix + mainCM.getMessagesFoundAnimals()
                            .replace("%player%", nearestPlayer.getName()).replace("%server_name%",mainCM.getPrefix())
                            .replace("%animal%", animalType.name()).replace("%world%", nearestPlayer.getWorld().getName())));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material consumedItem = event.getItem().getType();
        String consumedItemsString = String.valueOf(consumedItem).replace("_", " ");
        FoodManager foodManager = plugin.getFoodManager();
        MainConfigManager mainCM = plugin.getMainConfigManager();

        String overworld = mainCM.getConfigOverworld();
        String nether = mainCM.getConfigNether();
        String theEnd = mainCM.getConfigEnd();

        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerWorld = p.getWorld().getName();

            if (!playerWorld.equals(overworld) && !playerWorld.equals(nether) && !playerWorld.equals(theEnd)) {
                return;
            }
        }

        if (itemsDietaBalanceada.contains(consumedItem)) {
            if (!foodManager.isItemConsumed(consumedItem)){
                foodManager.addItem(consumedItem);
                if (mainCM.isMessagesFoundItemsBoolean()){
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage(Main.prefix + mainCM.getMessagesFoundItems()
                            .replace("%player%", player.getName()).replace("%server_name%",mainCM.getPrefix())
                            .replace("%item%", consumedItemsString).replace("%world%", player.getWorld().getName())));
                }
            }
        }
    }
}


