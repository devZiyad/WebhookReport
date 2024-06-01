package dev.plastec.webhookreport.commands;

import dev.plastec.webhookreport.utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class StatusCommand extends SubCommand {
    public StatusCommand(Plugin plugin, FileConfiguration config, String name, String permission) {
        super(plugin, config, name, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "you do not have permission to do that.");
            return false;
        }

        String publicWebhook = config.getString("webhook.public");

        String privateWebhook = config.getString("webhook.private");

        int cooldown = config.getInt("cooldown");

        StringBuilder builder = new StringBuilder();

        builder.append("WebhookReport's status:\n");

        builder.append("Public Webhook: ");

        if (publicWebhook == null || publicWebhook.isEmpty())
        {
            builder.append(ChatColor.RED);
            builder.append("null");
        }
        else {
            builder.append(ChatColor.GREEN);
            builder.append(publicWebhook);
        }
        builder.append("\n");
        builder.append(ChatColor.WHITE);


        builder.append("Private Webhook: ");

        if (privateWebhook == null || privateWebhook.isEmpty())
        {
            builder.append(ChatColor.RED);
            builder.append("null");
        }
        else {
            builder.append(ChatColor.GREEN);
            builder.append(publicWebhook);
        }
        builder.append("\n");
        builder.append(ChatColor.WHITE);

        builder.append("Cooldown: ");
        builder.append(ChatColor.GREEN);
        builder.append(cooldown);
        builder.append(" sec\n");
        builder.append(ChatColor.WHITE);

        builder.append("Session's Total Reports: ");
        builder.append(SendCommand.sessionReportCount);
        builder.append("\n");

        sender.sendMessage(builder.toString());
        return true;
    }
}
