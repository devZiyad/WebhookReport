package dev.plastec.webhookreport.commands;

import dev.plastec.webhookreport.utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class SetCommand extends SubCommand {
    public SetCommand(Plugin plugin, FileConfiguration config, String name, String permission) {
        super(plugin, config, name, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "you do not have permission to do that.");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "invalid command");

            StringBuilder builder = new StringBuilder("usage: /report set [ ");

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

        if (commandFound(args[0])) {
            SubCommand command = getCommand(args[0]);
            return command.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return false;
    }
}
