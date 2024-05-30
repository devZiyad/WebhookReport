package dev.plastec.webhookreport.commands;

import dev.plastec.webhookreport.utils.SubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;

public class ReportCommand implements CommandExecutor {
    public ArrayList<SubCommand> commands;
    Plugin plugin;
    FileConfiguration config;
    Permission permission;

    public ReportCommand(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.commands = new ArrayList<>();
        this.permission = new Permission("webhookreport");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "you do not have permission to do that.");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "invalid command!");

            StringBuilder builder = new StringBuilder("usage: /report [ ");
            for (SubCommand subCommand : commands) {
                if (sender.hasPermission(subCommand.getPermission())) {
                    builder.append(subCommand.name);
                    builder.append(" ");
                }
            }
            builder.append("]");

            sender.sendMessage(builder.toString());

            return false;
        }


        if (subCommandExists(args[0])) {
            SubCommand subCommand = getSubCommand(args[0]);
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommand.onCommand(sender, subArgs);
        }

        StringBuilder builder = new StringBuilder("/report ");
        builder.append(args[0]);
        builder.append(" is not a valid subcommand of /report");
        sender.sendMessage(ChatColor.RED + builder.toString());
        return false;
    }

    public SubCommand getSubCommand(String label) {
        String commandName;
        for (SubCommand command : commands) {
            commandName = command.getName();

            if (commandName.equalsIgnoreCase(label)) {
                return command;
            }
        }

        return null;
    }

    public boolean subCommandExists(String label) {
        for (SubCommand command : commands) {
            String commandName = command.getName();
            if (commandName.equalsIgnoreCase(label)) {
                return true;
            }
        }
        return false;
    }
}
