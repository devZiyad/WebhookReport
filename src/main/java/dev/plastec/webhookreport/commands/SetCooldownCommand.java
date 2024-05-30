package dev.plastec.webhookreport.commands;

import dev.plastec.webhookreport.utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;


public class SetCooldownCommand extends SubCommand {

    public SetCooldownCommand(Plugin plugin, FileConfiguration config, String name, String permission) {
        super(plugin, config, name, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "you do not have permission to do that.");
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments");
            sender.sendMessage(ChatColor.WHITE + "usage: /report set cooldown <cooldown>");
            return false;
        }

        boolean isNum = args[0].chars().allMatch(c -> c >= '0' && c <= '9');

        if (!isNum) {
            sender.sendMessage(ChatColor.RED + "<seconds> must be an integer number");
            return false;
        }

        sender.sendMessage(ChatColor.YELLOW + "Setting cooldown...");
        config.set("cooldown", Integer.parseInt(args[0]));
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Successfully changed cooldown");
        return true;
    }
}
