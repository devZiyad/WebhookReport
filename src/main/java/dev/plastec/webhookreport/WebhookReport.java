package dev.plastec.webhookreport;

import dev.plastec.webhookreport.commands.*;
import dev.plastec.webhookreport.tabcompletion.ReportTabCompletion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class WebhookReport extends JavaPlugin {
    FileConfiguration config;

    @Override
    public void onEnable() {
        // Setup default configuration
        config = this.getConfig();

        // Setup commands
        SetCommand setCommand = new SetCommand(this, config, "set", "webhookreport.set");

        SetWebhookCommand setWebhookCommand = new SetWebhookCommand(this, config, "webhook", "webhookreport.set.webhook");
        SetCooldownCommand setCooldownCommand = new SetCooldownCommand(this, config, "cooldown", "webhookreport.set.cooldown");

        setCommand.commands.add(setWebhookCommand);
        setCommand.commands.add(setCooldownCommand);

        SendCommand sendCommand = new SendCommand(this, config, "send", "webhookreport.send");

        StatusCommand statusCommand = new StatusCommand(this, config, "status", "webhookreport.status");

        ReportCommand reportCommand = new ReportCommand(this);
        reportCommand.commands.add(setCommand);
        reportCommand.commands.add(sendCommand);
        reportCommand.commands.add(statusCommand);

        ReportTabCompletion reportTabCompletion = new ReportTabCompletion();

        getCommand("report").setExecutor(reportCommand);
        getCommand("report").setTabCompleter(reportTabCompletion);

        // Config checks
        if (!config.isSet("webhook.public"))
            config.set("webhook.public", "");

        if (!config.isSet("webhook.private"))
            config.set("webhook.private", "");

        if (!config.isSet("cooldown"))
            config.set("cooldown", 15);

        getConfig().options().copyDefaults(true);
        saveConfig();

        String publicWebhook = config.getString("webhook.public");
        if (publicWebhook == null || publicWebhook.isEmpty()) {
            getLogger().log(Level.WARNING, "public webhook is not set. use the /report set public <webhook> command or change it in config.yml");
        }

        String privateWebhook = config.getString("webhook.private");
        if (privateWebhook == null || privateWebhook.isEmpty()) {
            getLogger().log(Level.WARNING, "private webhook is not set. use the /report set private <webhook> command or change it in config.yml");
        }

        // Schedule task for to clear reportsPer30Min count every 30 minutes
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                SendCommand.publicReportsPer30Min = 0;
                SendCommand.privateReportsPer30Min = 0;
            }
        }, 0, 20 * 60 * 30);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
