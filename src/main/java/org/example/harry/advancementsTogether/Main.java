package org.example.harry.advancementsTogether;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.harry.advancementsTogether.Commands.MainCommand;
import org.example.harry.advancementsTogether.Commands.MainCommandComplementer;
import org.example.harry.advancementsTogether.Listeners.BiomeListeners;
import org.example.harry.advancementsTogether.Listeners.MobsListeners;
import org.example.harry.advancementsTogether.Listeners.PlayerListeners;
import org.example.harry.advancementsTogether.config.MainConfigManager;
import org.example.harry.advancementsTogether.managers.AnimalsManager;
import org.example.harry.advancementsTogether.managers.BiomeManager;
import org.example.harry.advancementsTogether.managers.FoodManager;
import org.example.harry.advancementsTogether.managers.MobsManager;
import org.example.harry.advancementsTogether.model.ProgressFolderData;
import org.example.harry.advancementsTogether.utils.AdvancementsUtils;
import org.example.harry.advancementsTogether.utils.MessageUtils;

public final class Main extends JavaPlugin {

    //public static String prefix = "&8[ &5Adv&aTogether &8] ";
    public static String prefix;

    public static String name = "&5Adv&aTogether";

    private String version = getDescription().getVersion();
    private String author = String.valueOf(getDescription().getAuthors());

    private MainConfigManager mainConfigManager;
    private BiomeManager biomeManager;
    private AdvancementsUtils advancementsUtils;
    private MobsManager mobsManager;
    private AnimalsManager animalsManager;
    private FoodManager foodManager;

    @Override
    public void onEnable() {

        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&8============--------------------============"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("           "+name+ "&e Activated"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("            &ePlugin Version: &3"+version));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&8============--------------------============"));

        ProgressFolderData.createProgressFolder(getDataFolder(), getLogger());

        mainConfigManager = new MainConfigManager(this);

        prefix = getMainConfigManager().getPrefix();

        biomeManager = new BiomeManager(this);
        mobsManager = new MobsManager(this);
        animalsManager = new AnimalsManager(this);
        foodManager = new FoodManager(this);
        advancementsUtils = new AdvancementsUtils(this);

        registerCommands();
        registerEvents();

        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&7>> &eChecking Worlds"));
        if (areWorldsEmpty()) {
            getLogger().info("no worlds were found in the config.yml which will be loaded by default. The loaded worlds will count the achievements," +
                    " change in config.yml any problems.");
            checkConfig();
        }else {
            checkConfig();
        }
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&7>> &bThanks for using my first plugin, i hope you like it. z3 ATT: " + author ));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&7>> &bBugs or errors you can report them on the plugin page on spigot"));
    }

    @Override
    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&8============--------------------============"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("             "+name+ "&e Disabled"));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("             &ePlugin Version: &3"+version));
        Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("&8============--------------------============"));

        biomeManager.loadBiomes();
        mobsManager.loadMobs();
        animalsManager.loadAnimals();
        foodManager.loadItems();
    }

    public void registerCommands(){
        this.getCommand("advtogether").setExecutor(new MainCommand(this));
        this.getCommand("advtogether").setTabCompleter(new MainCommandComplementer());
    }

    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerListeners(this, advancementsUtils),this);
        getServer().getPluginManager().registerEvents(new BiomeListeners(this,biomeManager),this);
        getServer().getPluginManager().registerEvents(new MobsListeners(this,mobsManager),this);
    }

    public MainConfigManager getMainConfigManager() {
        return mainConfigManager;
    }

    public BiomeManager getBiomeManager(){
        return biomeManager;
    }

    public MobsManager getMobsManager(){return mobsManager;}

    public AnimalsManager getAnimalsManager(){return animalsManager;}

    public FoodManager getFoodManager(){return foodManager;}

    private boolean areWorldsEmpty() {
        return mainConfigManager.getConfigOverworld() == null &&
                mainConfigManager.getConfigNether() == null &&
                mainConfigManager.getConfigEnd() == null;
    }


    private void checkConfig() {
        //Configuration values
        String overworld = getMainConfigManager().getConfigOverworld();
        String nether = getMainConfigManager().getConfigNether();
        String theEnd = getMainConfigManager().getConfigEnd();

        if (overworld != null) {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("  &8> &bOverworld: &a"+ overworld));
        } else {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("  &8> &eOverworld is missing or invalid in config.yml"));
        }

        if (nether != null) {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("  &8> &cNether: &a" + nether));
        } else {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("  &8> &eNether is missing or invalid in config.yml"));
        }

        if (theEnd != null) {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("  &8> &5End: &a" + theEnd));
        } else {
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage("  &8> &eThe End is missing or invalid in config.yml"));
        }

        getLogger().info("Config check complete.");
    }
}