package org.example.harry.advancementsTogether.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.harry.advancementsTogether.Main;
import org.example.harry.advancementsTogether.config.MainConfigManager;
import org.example.harry.advancementsTogether.managers.AnimalsManager;
import org.example.harry.advancementsTogether.managers.BiomeManager;
import org.example.harry.advancementsTogether.managers.FoodManager;
import org.example.harry.advancementsTogether.managers.MobsManager;
import org.example.harry.advancementsTogether.utils.MessageUtils;


public class MainCommand implements CommandExecutor {

    private Main plugin;

    public MainCommand(Main plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    MainConfigManager mainConfigManager = plugin.getMainConfigManager();
                    sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesCommandsReload()));
                    plugin.getMainConfigManager().reloadConfig();
                    plugin.getBiomeManager().reloadBiomes();
                    plugin.getMobsManager().reloadMobs();
                    plugin.getAnimalsManager().reloadAnimals();
                    Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(Main.prefix + "&8[Console]"+ mainConfigManager.getMessagesCommandsReload()));
                    return true;
                } else if (args[0].equalsIgnoreCase("help")) {
                    helpConsole(sender);
                    return true;
                } else if (args[0].equalsIgnoreCase("progress")){
                    subCommandProgressConsole(sender, args);
                    return true;
                } else if (args[0].equalsIgnoreCase("awake")) {
                    sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix+ "&eThis command can only be used by one players!"));
                } else if (args[0].equalsIgnoreCase("info")) {
                    sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix+ "&eThis command can only be used by one players!"));
                } else {
                    helpConsole(sender);
                }
            }
            //Consola
            helpConsole(sender);
            return true;
        }

        //Player
        Player player = (((Player) sender).getPlayer());

        // /HT args[0] args[1] args[2]
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                // /alladv reload
                subCommandReload(sender);
            } else if (args[0].equalsIgnoreCase("progress")) {
            subCommandProgress(sender, args);
            } else if (args[0].equalsIgnoreCase("help")) {
                help(sender);
            } else if (args[0].equalsIgnoreCase("awake")) {
                subCommandAwake(sender);
            } else if (args[0].equalsIgnoreCase("info")){
                subCommandInfo(sender);
            }
            else {
                // /alladv
                help(sender);
            }
        } else {
            // /alladv
            help(sender);
        }
        return true;
    }
    // CONSOLE
    public void helpConsole(CommandSender sender){
        sender.sendMessage(MessageUtils.getColoredMessage("&7--------[AdvTogether] Commands Console--------"));
        sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether reload"));
        sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether progress <biomes>/<mobs>"));
    }
    public void subCommandProgressConsole(CommandSender sender, String[] args){

        MainConfigManager mainConfigManager = plugin.getMainConfigManager();
        BiomeManager biomeManager = plugin.getBiomeManager();
        MobsManager mobsManager = plugin.getMobsManager();
        AnimalsManager animalsManager = plugin.getAnimalsManager();
        FoodManager foodManager = plugin.getFoodManager();
        // /alladv progress

        if (args.length <= 1) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + "&cYou need to use /advtogether progress <biomes>/<mobs>/<breeds>/<items>"));
            return;
        }
        // /alladv progress <biomes>/<mobs>
        // /alladv args[0] args[1] args[2]+

        // /alladv progress biomes
        if (args[1].equalsIgnoreCase("biomes")) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesCommandsBiomes() +"&a"+ biomeManager.getBiomesCount() +"/53"));
            plugin.getBiomeManager().getBiomesList(sender);

            // /alladv mobs
        }else if (args[1].equalsIgnoreCase("mobs")) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesCommandsMobs() +"&a"+mobsManager.getMobsCount()+"/36"));
            plugin.getMobsManager().getMobsList(sender);
            // /advtogether breeds
        } else if (args[1].equalsIgnoreCase("breeds")) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix+ mainConfigManager.getMessagesCommandsAnimals() +"&a"+animalsManager.getAnimalCount()+"/25"));
            plugin.getAnimalsManager().getMobsList(sender);
        } else if (args[1].equalsIgnoreCase("food")){
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix+ mainConfigManager.getMessagesCommandsItems() +"&a"+foodManager.getItemCount()+"/40"));
            plugin.getFoodManager().getItemsList(sender);
        } else {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + "&cYou need to use /advtogether progress <biomes>/<mobs>/<breeds>/<food>"));
        }
    }

    //PLAYER
    public void help(CommandSender sender){
        Player player = (Player) sender;
        if (player.hasPermission("advancementTogether.commands")){
            sender.sendMessage(MessageUtils.getColoredMessage("&7-----------[AdvTogether] Commands-----------"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether info"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether awake"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether progress <biomes>/<mobs>/<food>/<breeds>"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7- [op] /advtogether reload"));
        }else {
            sender.sendMessage(MessageUtils.getColoredMessage("&7-----------[AdvTogether] Commands-----------"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether info"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether awake"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7- /advtogether progress <biomes>/<mobs>/<food>/<breeds>"));
        }
    }
    public void subCommandReload(CommandSender sender){
        //at reload
        MainConfigManager mainConfigManager = plugin.getMainConfigManager();
        if (sender.hasPermission("advancementTogether.commands")) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesCommandsReload()));
            plugin.getMainConfigManager().reloadConfig();
            plugin.getBiomeManager().reloadBiomes();
            plugin.getMobsManager().reloadMobs();
            plugin.getAnimalsManager().reloadAnimals();
            Bukkit.getConsoleSender().sendMessage(MessageUtils.getColoredMessage(Main.prefix+ "&8[Console] "+ mainConfigManager.getMessagesCommandsReload()));
        }else {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + "&c¡You do not have permission to use this command!"));
        }
    }
    public void subCommandProgress(CommandSender sender, String[] args){

        Player player = (Player) sender;

        MainConfigManager mainConfigManager = plugin.getMainConfigManager();
        BiomeManager biomeManager = plugin.getBiomeManager();
        MobsManager mobsManager = plugin.getMobsManager();
        AnimalsManager animalsManager = plugin.getAnimalsManager();
        FoodManager foodManager = plugin.getFoodManager();
        // /alladv progress

        if (args.length <= 1) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + "&cYou need to use /advtogether progress <biomes>/<mobs>/<food>/<breeds>"));
            return;
        }
        // /alladv progress <biomes>/<mobs>
        // /alladv args[0] args[1] args[2]+

        // /alladv progress biomes
        if (args[1].equalsIgnoreCase("biomes")) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesCommandsBiomes() +"&a"+ biomeManager.getBiomesCount() +"/53"));
            plugin.getBiomeManager().getBiomesList(sender);

            // /alladv mobs
        }else if (args[1].equalsIgnoreCase("mobs")) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + mainConfigManager.getMessagesCommandsMobs() +"&a"+mobsManager.getMobsCount()+"/36"));
            plugin.getMobsManager().getMobsList(sender);
            // /advtogether breeds
        } else if (args[1].equalsIgnoreCase("breeds")) {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix+ mainConfigManager.getMessagesCommandsAnimals() +"&a"+animalsManager.getAnimalCount()+"/25"));
            plugin.getAnimalsManager().getMobsList(sender);
        } else if (args[1].equalsIgnoreCase("food")){
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix+ mainConfigManager.getMessagesCommandsItems() +"&a"+foodManager.getItemCount()+"/40"));
            plugin.getFoodManager().getItemsList(sender);
        }
        else {
            sender.sendMessage(MessageUtils.getColoredMessage(Main.prefix + "&cYou need to use /advtogether progress <biomes>/<mobs>/<food>/<breeds>"));
        }
    }
    public void subCommandAwake(CommandSender sender){

        Player p = (Player) sender;

        int timeAwake = ((Player) p).getStatistic(Statistic.TIME_SINCE_REST) / 20;

        long hours = timeAwake % 86400 / 3600;
        long minutes = (timeAwake % 3600) / 60;
        long seconds = timeAwake % 60;
        long days = timeAwake / 86400;

        String awake = (days >= 1 ? days + " days " : "") + String.format("%02d:%02d:%02d", hours, minutes, seconds);

        p.sendMessage(MessageUtils.getColoredMessage(Main.prefix+ "&aTime Awake: &b" + awake));
    }
    public void subCommandInfo(CommandSender sender){
        Player player = (Player) sender;
        if(player.hasPermission("advancementTogether.commands")){
            sender.sendMessage(MessageUtils.getColoredMessage("&7    -----------&a[&5Adv&aTogether&a]&7 Info-----------"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7achievements together is a plugin which you can get all achievements together with your friends."));
            sender.sendMessage(MessageUtils.getColoredMessage("&7[op] you can configure all the messages from the config file or disable them for an easier experience."));
            sender.sendMessage(MessageUtils.getColoredMessage(""));
            sender.sendMessage(MessageUtils.getColoredMessage("&7you can also use <progress> for information on achievements"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7    -----------&a[&5Adv&aTogether&a]&7 Info-----------"));
        }else {
            sender.sendMessage(MessageUtils.getColoredMessage("&7    -----------&a[&5Adv&aTogether&a]&7 Info-----------"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7achievements together is a plugin which you can get all achievements together with your friends."));
            sender.sendMessage(MessageUtils.getColoredMessage(""));
            sender.sendMessage(MessageUtils.getColoredMessage("&7you can also use <progress> for information on achievements"));
            sender.sendMessage(MessageUtils.getColoredMessage("&7    -----------&a[&5Adv&aTogether&a]&7 Info-----------"));
        }
    }
}