package dev.plastec.webhookreport.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public abstract class SubCommand {
    public Plugin plugin;
    public FileConfiguration config;
    public String name;
    public Permission permission;
    public ArrayList<SubCommand> commands;

    public SubCommand(Plugin plugin, FileConfiguration config, String name, String permission) {
        this.plugin = plugin;
        this.config = config;
        this.name = name;
        this.commands = new ArrayList<>();
        this.permission = new Permission(permission);
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public Permission getPermission() {
        return permission;
    }

    public SubCommand getCommand(String label) {
        String commandName;
        for (SubCommand command : commands) {
            commandName = command.getName();

            if (commandName.equals(label)) {
                return command;
            }
        }

        return null;
    }

    public boolean commandFound(String label) {
        for (SubCommand command : commands) {
            String commandName = command.getName();
            if (commandName.equals(label)) {
                return true;
            }
        }
        return false;
    }
}