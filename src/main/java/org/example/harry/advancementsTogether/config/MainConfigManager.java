package org.example.harry.advancementsTogether.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.example.harry.advancementsTogether.Main;

import java.util.List;
import java.util.Map;

public class MainConfigManager {

    private CustomConfig configFile;

    private Main plugin;

    private String prefix;

    private List<String> messajesWelcomeMessage;
    private boolean configWelcomeMessageBolean;
    //worlds
    private String configOverworld;
    private String configNether;
    private String configEnd;
    //Welcome Advancement
    private boolean messagesWelcomeAchivementBoolean;
    private String messagesWelcomeAchivementTitle;
    private String messagesWelcomeAchivementDescription;
    private String messagesWelcomeAchivementFrame;
    private String messagesWelcomeAchivementIcon;
    private String messagesWelcomeAchivementBackground;
    //Messages Obtain Advancement
    private String messageObtainAdvancement;
    private boolean messageObtainAdvancementBoolean;
    private String messageObtainAdvancementSound;
    private String messageObtainAdvancementParticle;
    //Actionbar
    private boolean messageObtainActionBarBoolean;
    private String messageObtainActionBar;
    //Messages Found Biomes
    private boolean messagesFoundBiomesBoolean;
    private String messagesFoundBiomes;
    //Messages Found Mobs
    private boolean messagesFoundMobsBoolean;
    private String messagesFoundMobs;
    //Messages Found Animals
    private boolean messagesFoundAnimalsBoolean;
    private String messagesFoundAnimals;
    //Messages Found Items
    private boolean messagesFoundItemsBoolean;
    private String messagesFoundItems;
    //Messages Commands
    private String messagesCommandsReload;
    private String messagesCommandsBiomes;
    private String messagesCommandsMobs;
    private String messagesCommandsAnimals;
    private String messagesCommandsItems;

    public MainConfigManager(Main plugin){
        this.plugin = plugin;
        configFile = new CustomConfig("config.yml", null,plugin, false);
        configFile.registerConfig();
        loadConfig();
    }
    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        prefix = config.getString("Config.Prefix", "&8[ &5Adv&aTogether &8] ");

        messajesWelcomeMessage = config.getStringList("Config.Welcome_message.message");
        configWelcomeMessageBolean = config.getBoolean("Config.Welcome_message.enabled", true);

        configOverworld = validateWorld(config.getString("Config.Worlds.overworld", "world"));
        configNether = validateWorld(config.getString("Config.Worlds.nether", "world_nether"));
        configEnd = validateWorld(config.getString("Config.Worlds.the_end", "world_the_end"));

        messagesWelcomeAchivementBoolean = config.getBoolean("Config.Achievement_display_welcome.enabled", true);
        messagesWelcomeAchivementTitle = config.getString("Config.Achievement_display_welcome.title", "&f&l¡Welcome, &6%player%!");
        messagesWelcomeAchivementDescription = config.getString("Config.Achievement_display_welcome.description", "&5&kd &a¡Thanks for playing &5&kd &6%player%!");
        messagesWelcomeAchivementFrame = config.getString("Config.Achievement_display_welcome.frame", "goal");
        messagesWelcomeAchivementIcon = config.getString("Config.Achievement_display_welcome.icon", "AXOLOTL_BUCKET");
        messagesWelcomeAchivementBackground = config.getString("Config.Achievement_display_welcome.background", "BLACK_CONCRETE_POWDER");

        messageObtainAdvancement = config.getString("Config.Obtain_advancement_message.achievement_obtained", "&eCongratulations you got the achievement: &a[%advancement%]");
        messageObtainAdvancementBoolean = config.getBoolean("Config.Obtain_advancement_message.achievement_obtained_message", true);
        messageObtainAdvancementSound = config.getString("Config.Obtain_advancement_message.achievement_sound_obtained", "BLOCK_NOTE_BLOCK_BELL");
        messageObtainAdvancementParticle = config.getString("Config.Obtain_advancement_message.achievement_particle_obtained", "HAPPY_VILLAGER");

        messageObtainActionBarBoolean = config.getBoolean("Config.Obtain_advancement_message.action_bar_message", true);
        messageObtainActionBar = config.getString("Config.Obtain_advancement_message.achievement_obtained_action_bar", "&eAchievement completed &a[%advancement%]");

        messagesFoundBiomesBoolean = config.getBoolean("Config.Biomes_found.biomes_found", true);
        messagesFoundBiomes = config.getString("Config.Biomes_found.biomes_found_message", "&aNew biome has been found: &b%biome%");

        messagesFoundMobsBoolean = config.getBoolean("Config.Mobs_found.mobs_found", true);
        messagesFoundMobs = config.getString("Config.Mobs_found.mobs_found_message", "&aNew mob has been found: &5%mob%");

        messagesFoundAnimalsBoolean = config.getBoolean("Config.Animals_reproduced.animals_found", true);
        messagesFoundAnimals = config.getString("Config.Animals_reproduced.animals_found_message", "&aNew animal has been reproduced: &5%animal%");

        messagesFoundItemsBoolean = config.getBoolean("Config.Items_consume.items_found", true);
        messagesFoundItems = config.getString("Config.Items_consume.items_found_message", "&aNew item has been consumed: &5%item%");
        //Menssages Commands
        messagesCommandsReload = config.getString("Config.Command_messages.command_reload", "&eThe configuration was reloaded");
        messagesCommandsBiomes = config.getString("Config.Command_messages.command_get_biomes", "&eDiscovered Biomes: ");
        messagesCommandsMobs = config.getString("Config.Command_messages.command_get_mobs", "&eDiscovered Mobs: ");
        messagesCommandsAnimals = config.getString("Config.Command_messages.command_get_animals", "&eAnimals Reproduced: ");
        messagesCommandsItems = config.getString("Config.Command_messages.command_get_items", "&eItems Consumed: ");

    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getPrefix(){return  prefix;
    }

    public List<String> getMessajesWelcomeMessage() {return messajesWelcomeMessage;
    }

    public boolean isConfigWelcomeMessageBolean(){return configWelcomeMessageBolean;
    }

    public boolean isMessagesWelcomeAchivementBoolean(){return messagesWelcomeAchivementBoolean;}

    public String getMessagesWelcomeAchivementTitle(){return messagesWelcomeAchivementTitle;
    }

    public String getMessagesWelcomeAchivementDescription(){return messagesWelcomeAchivementDescription;
    }

    public String getMessagesWelcomeAchivementFrame(){return messagesWelcomeAchivementFrame;
    }

    public String getMessagesWelcomeAchivementIcon(){return messagesWelcomeAchivementIcon;
    }

    public String getMessagesWelcomeAchivementBackground(){return messagesWelcomeAchivementBackground;
    }

    public String getMessageObtainAdvancement(){return messageObtainAdvancement;
    }

    public boolean isMessageObtainAdvancementBoolean(){return messageObtainAdvancementBoolean;
    }

    public String getMessageObtainAdvancementSound(){return messageObtainAdvancementSound;
    }

    public String getMessageObtainAdvancementParticle(){return messageObtainAdvancementParticle;
    }

    public String getMessageObtainActionBar(){return messageObtainActionBar;
    }

    public boolean isMessageObtainActionBarBoolean(){return messageObtainActionBarBoolean;
    }

    public boolean isMessagesFoundBiomesBoolean(){return  messagesFoundBiomesBoolean;}

    public String getMessagesFoundBiomes(){return messagesFoundBiomes;
    }

    public boolean isMessagesFoundMobsBoolean(){return messagesFoundMobsBoolean;}

    public String getMessagesFoundMobs(){return messagesFoundMobs;}

    public boolean isMessagesFoundAnimalsBoolean(){return messagesFoundAnimalsBoolean;}

    public String getMessagesFoundAnimals(){return messagesFoundAnimals;}

    public boolean isMessagesFoundItemsBoolean(){return messagesFoundItemsBoolean;}

    public String getMessagesFoundItems(){return messagesFoundItems;}


    private String validateWorld(String worldName) {
        if (worldName != null && Bukkit.getWorld(worldName) != null) {
            return worldName;
        }
        return null;
    }

    public String getConfigOverworld(){return configOverworld;}

    public String getConfigNether(){return configNether;}

    public String getConfigEnd(){return configEnd;}

    public String getMessagesCommandsReload(){return messagesCommandsReload;}

    public String getMessagesCommandsBiomes(){return messagesCommandsBiomes;}

    public String getMessagesCommandsMobs(){return messagesCommandsMobs;}

    public String getMessagesCommandsAnimals(){return messagesCommandsAnimals;}

    public String getMessagesCommandsItems(){return messagesCommandsItems;}
}