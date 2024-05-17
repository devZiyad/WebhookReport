package dev.plastec.webhookreport.commands;

import dev.plastec.webhookreport.utils.DiscordWebhook;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportCommand implements CommandExecutor {
    Plugin plugin;
    FileConfiguration configuration;
    Map<String, Long> durationBetweenSend;
    int sendReportCooldown;

    public ReportCommand(Plugin plugin) {
        this.plugin = plugin;
        this.configuration = plugin.getConfig();
        this.durationBetweenSend = new HashMap<>();
        this.sendReportCooldown = configuration.getInt("cooldown");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (sender.hasPermission("webhookreport"))
                sender.sendMessage(ChatColor.RED + "specify a command (set or send)");
            else
                sender.sendMessage("You do not have permission to do that");
            return false;
        }

        String[] arguments = Arrays.copyOfRange(args, 1, args.length);

        switch (args[0]) {
            case "set":
                boolean canSetWebhook = sender.hasPermission("webhookreport.setwebhook");
                boolean canSetCooldown = sender.hasPermission("webhookreport.setcooldown");
                boolean canSet = canSetWebhook | canSetCooldown;

                if (!canSet) {
                    sender.sendMessage("You do not have permission to do that");
                    return false;
                }

                if (args[1].equalsIgnoreCase("webhook") && canSetWebhook) {
                    return setWebhook(sender, args[2]);
                }

                if (args[1].equalsIgnoreCase("cooldown") && canSetCooldown) {
                    return setCooldown(sender, args[2]);
                }

            case "send":
                if (!(sender.hasPermission("webhookreport.sendreport"))) {
                    sender.sendMessage("You do not have permission to do that");
                    return false;
                }
                return sendReport(sender, arguments);
            default:
                sender.sendMessage(ChatColor.RED + "invalid argument: " + args[0]);
                return false;
        }
    }

    public boolean sendReport(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only a player is allowed to use this command");
            return false;
        }

        boolean useCooldown = !player.hasPermission("webhookreport.bypasscooldown");

        if (useCooldown) {
            long timeElapsed;
            if (durationBetweenSend.containsKey(player.getName())) {
                timeElapsed = new Date().getTime() - durationBetweenSend.get(player.getName());
            } else {
                timeElapsed = sendReportCooldown * 1000L;
            }

            if (timeElapsed < (sendReportCooldown * 1000L)) {
                sender.sendMessage(ChatColor.RED + "Report is on cooldown. You will be able to use it within " + (sendReportCooldown - (timeElapsed / 1000)) + " seconds");
                return false;
            }
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Invalid arguments");
            return false;
        }

        if (!(args[0].equalsIgnoreCase("public") || args[0].equalsIgnoreCase("private"))) {
            player.sendMessage(ChatColor.RED + "Did not specify public or private report");
            return false;
        }

        String webhookURL = configuration.getString("webhook");

        if (webhookURL.equalsIgnoreCase("null")) {
            sender.sendMessage(ChatColor.RED + "WebhookReport is not set up properly. Contact server administration");
            return false;
        }

        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookURL);
            StringBuilder stringBuilder = new StringBuilder();

            if (args[0].equalsIgnoreCase("private")) {
                stringBuilder.append("Anonymous");
            } else {
                stringBuilder.append(player.getName());
            }

            stringBuilder.append(" report:\\n```");
            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]);
                if (i != args.length - 1) {
                    stringBuilder.append(" ");
                }
            }
            stringBuilder.append("```");

            webhook.setContent(stringBuilder.toString());
            player.sendMessage(ChatColor.YELLOW + "Submitting report...");
            webhook.execute();
            player.sendMessage(ChatColor.GREEN + "Successfully submitted report");
            durationBetweenSend.put(sender.getName(), new Date().getTime());
            return true;
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Could not submit report. Please contact server administration");
            plugin.getLogger().log(Level.WARNING, e.toString());
            return false;
        }
    }

    public boolean setWebhook(CommandSender sender, String url) {
        Pattern pattern = Pattern.compile("^https://discord\\.com/api/webhooks/[\\d]+/[a-zA-Z0-9_-]+$");
        Matcher matcher = pattern.matcher(url);
        boolean validWebhook = matcher.find();

        if (!validWebhook) {
            sender.sendMessage(ChatColor.RED + "Invalid webhook format");
            plugin.getLogger().log(Level.WARNING, "Invalid webhook format");
            return false;
        }

        try {
            sender.sendMessage(ChatColor.YELLOW + "Setting webhook...");
            configuration.set("webhook", url);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Successfully changed webhook");
            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to set webhook");
            plugin.getLogger().log(Level.WARNING, e.toString());
            return false;
        }
    }

    public boolean setCooldown(CommandSender sender, String cooldown) {
        boolean isNum = cooldown.chars().allMatch(c -> c >= '0' && c <= '9');

        if (!isNum) {
            sender.sendMessage(ChatColor.RED + "<seconds> must be an integer number");
            return false;
        }

        sender.sendMessage(ChatColor.YELLOW + "Setting cooldown...");
        configuration.set("cooldown", Integer.parseInt(cooldown));
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Successfully changed cooldown");
        return true;
    }
}
