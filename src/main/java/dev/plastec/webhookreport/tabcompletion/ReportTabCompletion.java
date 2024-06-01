package dev.plastec.webhookreport.tabcompletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReportTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }

        boolean canSetWebhook = sender.hasPermission("webhookreport.set.webhook");
        boolean canSetCooldown = sender.hasPermission("webhookreport.set.cooldown");
        boolean canSet = canSetWebhook | canSetCooldown;
        boolean canSend = sender.hasPermission("webhookreport.send");
        boolean canStatus = sender.hasPermission("webhookreport.status");

        if (args.length == 1) {
            List<String> completion = new ArrayList<>();
            if (canSet) {
                completion.add("set");
            }

            if (canSend) {
                completion.add("send");
            }

            if (canStatus) {
                completion.add("status");
            }

            return completion;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set") && canSet) {
                List<String> completion = new ArrayList<>();

                if (canSetWebhook) {
                    completion.add("webhook");
                }

                if (canSetCooldown) {
                    completion.add("cooldown");
                }

                return completion;
            }

            if (args[0].equalsIgnoreCase("send") && canSend) {
                return List.of("public", "private");
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set") && canSet) {
                if (args[1].equalsIgnoreCase("webhook") && canSetWebhook) {
                    return List.of("public", "private");
                }

                if (args[1].equalsIgnoreCase("cooldown") && canSetCooldown) {
                    return List.of("<seconds>");
                }
            }

            if (args[0].equalsIgnoreCase("send") && canSend) {
                return List.of("<message>");
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("set") && canSet) {
                if (args[1].equalsIgnoreCase("webhook") && canSetWebhook) {
                    return List.of("<url>");
                }
            }
        }

        return List.of();
    }
}
