package org.example.harry.advancementsTogether.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainCommandComplementer implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender Sender, Command command, String s, String[] args) {

        List<String> subcommands = new ArrayList<>();

        if (args.length == 1){
            subcommands.add("help");
            subcommands.add("progress");
            subcommands.add("info");
            subcommands.add("awake");

            if (Sender.hasPermission("advancementTogether.commands")){
                subcommands.add("reload");
            }
        }

        if (args.length == 2){

            if (args[0].equalsIgnoreCase("progress")){
                subcommands.add("biomes");
                subcommands.add("mobs");
                subcommands.add("breeds");
                subcommands.add("food");
            }
        }

        return subcommands;
    }
}
