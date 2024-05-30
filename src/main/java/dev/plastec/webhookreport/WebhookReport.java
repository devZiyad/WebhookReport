package dev.plastec.webhookreport;

import dev.plastec.webhookreport.commands.*;
import dev.plastec.webhookreport.tabcompletion.ReportTabCompletion;
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

        ReportCommand reportCommand = new ReportCommand(this);
        reportCommand.commands.add(setCommand);
        reportCommand.commands.add(sendCommand);

        ReportTabCompletion reportTabCompletion = new ReportTabCompletion();

        getCommand("report").setExecutor(reportCommand);
        getCommand("report").setTabCompleter(reportTabCompletion);

        if (!config.isSet("webhook.public"))
            config.set("webhook.public", "");

        if (!config.isSet("webhook.private"))
            config.set("webhook.private", "");

        if (!config.isSet("cooldown"))
            config.set("cooldown", 15);

        getConfig().options().copyDefaults(true);
        saveConfig();

        if (config.getString("webhook.public").isEmpty()) {
            getLogger().log(Level.WARNING, "public webhook is not set. use the /report set public <webhook> command or change it in config.yml");
        }

        if (config.getString("webhook.private").isEmpty()) {
            getLogger().log(Level.WARNING, "private webhook is not set. use the /report set private <webhook> command or change it in config.yml");
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
