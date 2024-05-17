package dev.plastec.webhookreport;

import dev.plastec.webhookreport.commands.ReportCommand;
import dev.plastec.webhookreport.commands.ReportTabCompletion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class WebhookReport extends JavaPlugin {
    FileConfiguration config;

    @Override
    public void onEnable() {
        // Add commands
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("report").setTabCompleter(new ReportTabCompletion());

        // Setup default configuration
        config = this.getConfig();

        if (!config.isSet("webhook"))
            config.set("webhook", "null");

        if (!config.isSet("cooldown"))
            config.set("cooldown", 15);

        getConfig().options().copyDefaults(true);
        saveConfig();

        if (config.getString("webhook").equalsIgnoreCase("null")) {
            getLogger().log(Level.WARNING,"webhook is not set. use the /report set <webhook> command or change it in config.yml");
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
