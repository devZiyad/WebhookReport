package dev.plastec.webhookreport.commands;

import dev.plastec.webhookreport.utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetWebhookCommand extends SubCommand {
    public SetWebhookCommand(Plugin plugin, FileConfiguration config, String name, String permission) {
        super(plugin, config, name, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "you do not have permission to do that.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments");
            sender.sendMessage(ChatColor.WHITE + "usage: /report set webhook [public/private] <webhook_url>");
            return false;
        }

        Pattern pattern = Pattern.compile("^https://discord\\.com/api/webhooks/[\\d]+/[a-zA-Z0-9_-]+$");
        Matcher matcher = pattern.matcher(args[1]);
        boolean validWebhook = matcher.find();

        if (!validWebhook) {
            sender.sendMessage(ChatColor.RED + "Invalid webhook format");
            return false;
        }

        try {
            sender.sendMessage(ChatColor.YELLOW + "Setting " + args[0] + " webhook...");
            config.set("webhook." + args[0], args[1]);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Successfully changed " + args[0] + " webhook");
            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to set webhook");
            return false;
        }
    }
}
