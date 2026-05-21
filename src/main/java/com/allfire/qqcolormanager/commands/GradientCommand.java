package com.allfire.qqcolormanager.commands;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.config.GradientConfig;
import com.allfire.qqcolormanager.util.ColorUtil;
import com.allfire.qqcolormanager.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class GradientCommand {
    
    private final QQColorManager plugin;

    public GradientCommand(QQColorManager plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(sender, "<red>Usage: /qqcm gradient <set|get|remove> ...");
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "set":
                handleSet(sender, args);
                break;
            case "get":
                handleGet(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            default:
                MessageUtil.send(sender, "<red>Unknown gradient action: " + action);
        }
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (args.length < 5) {
            MessageUtil.send(sender, "<red>Usage: /qqcm gradient set <id> <slot> <color> [player] [-s]");
            return;
        }

        String id = args[2];
        int slot;
        try {
            slot = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            MessageUtil.send(sender, "<red>Slot must be a number");
            return;
        }

        String colorInput = args[4];
        String playerName = null;
        boolean silent = false;

        for (int i = 5; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-s")) {
                silent = true;
            } else if (playerName == null) {
                playerName = args[i];
            }
        }

        if (playerName != null && !sender.hasPermission("qqcm.gradient.set.other")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }
        if (playerName == null && !(sender instanceof Player)) {
            sender.sendMessage("Console must specify a player");
            return;
        }
        if (playerName == null && !sender.hasPermission("qqcm.gradient.set")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        GradientConfig gradient = plugin.getConfigManager().getGradient(id);
        if (gradient == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("template_not_found"), silent);
            return;
        }

        if (slot < 1 || slot > gradient.getSlots()) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("slot_not_found"), silent);
            return;
        }

        String hex = ColorUtil.extractHex(colorInput);
        if (hex == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid_color"), silent);
            return;
        }

        String regex = gradient.getInputRegex();
        if (regex != null && !Pattern.matches(regex, hex)) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid_regex")
                .replace("<regex>", regex), silent);
            return;
        }

        UUID targetUuid;
        String targetName;
        if (playerName == null) {
            targetUuid = ((Player) sender).getUniqueId();
            targetName = sender.getName();
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (!target.hasPlayedBefore() && target.getName() == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("player_not_found"), silent);
                return;
            }
            targetUuid = target.getUniqueId();
            targetName = target.getName() != null ? target.getName() : playerName;
        }

        plugin.getStorage().setGradientColor(targetUuid, targetName, id, slot, hex);

        if (!silent) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("gradient_set")
                .replace("<slot>", String.valueOf(slot))
                .replace("<color>", "#" + hex));
        }
    }

    private void handleGet(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil.send(sender, "<red>Usage: /qqcm gradient get <id> [player] [-s]");
            return;
        }

        String id = args[2];
        String playerName = null;
        boolean silent = false;

        for (int i = 3; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-s")) {
                silent = true;
            } else if (playerName == null) {
                playerName = args[i];
            }
        }

        if (playerName != null && !sender.hasPermission("qqcm.gradient.get.other")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }
        if (playerName == null && !(sender instanceof Player)) {
            sender.sendMessage("Console must specify a player");
            return;
        }
        if (playerName == null && !sender.hasPermission("qqcm.gradient.get")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        GradientConfig gradient = plugin.getConfigManager().getGradient(id);
        if (gradient == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("template_not_found"), silent);
            return;
        }

        UUID targetUuid;
        if (playerName == null) {
            targetUuid = ((Player) sender).getUniqueId();
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (!target.hasPlayedBefore() && target.getName() == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("player_not_found"), silent);
                return;
            }
            targetUuid = target.getUniqueId();
        }

        if (!silent) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("gradient_get"));
            for (int slot = 1; slot <= gradient.getSlots(); slot++) {
                String color = plugin.getStorage().getGradientColor(targetUuid, id, slot);
                if (color != null) {
                    MessageUtil.send(sender, "  <yellow>Slot " + slot + ": <white>#" + color);
                } else {
                    String fallback = gradient.getFallbackColor(slot);
                    MessageUtil.send(sender, "  <gray>Slot " + slot + ": <dark_gray>not set <gray>(fallback: #" + fallback + ")");
                }
            }
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtil.send(sender, "<red>Usage: /qqcm gradient remove <id> [player] [-s]");
            return;
        }

        String id = args[2];
        String playerName = null;
        boolean silent = false;

        for (int i = 3; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-s")) {
                silent = true;
            } else if (playerName == null) {
                playerName = args[i];
            }
        }

        if (playerName != null && !sender.hasPermission("qqcm.gradient.remove.other")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }
        if (playerName == null && !(sender instanceof Player)) {
            sender.sendMessage("Console must specify a player");
            return;
        }
        if (playerName == null && !sender.hasPermission("qqcm.gradient.remove")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        GradientConfig gradient = plugin.getConfigManager().getGradient(id);
        if (gradient == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("template_not_found"), silent);
            return;
        }

        UUID targetUuid;
        if (playerName == null) {
            targetUuid = ((Player) sender).getUniqueId();
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (!target.hasPlayedBefore() && target.getName() == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("player_not_found"), silent);
                return;
            }
            targetUuid = target.getUniqueId();
        }

        plugin.getStorage().removeGradient(targetUuid, id);

        if (!silent) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("gradient_removed")
                .replace("<gradient>", id));
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 2) {
            List<String> actions = new ArrayList<>();
            if (sender.hasPermission("qqcm.gradient.set") || sender.hasPermission("qqcm.gradient.set.other")) {
                actions.add("set");
            }
            if (sender.hasPermission("qqcm.gradient.get") || sender.hasPermission("qqcm.gradient.get.other")) {
                actions.add("get");
            }
            if (sender.hasPermission("qqcm.gradient.remove") || sender.hasPermission("qqcm.gradient.remove.other")) {
                actions.add("remove");
            }
            for (String action : actions) {
                if (action.startsWith(args[1].toLowerCase())) {
                    completions.add(action);
                }
            }
            return completions;
        }
        
        if (args.length == 3) {
            String action = args[1].toLowerCase();
            if (action.equals("set") || action.equals("get") || action.equals("remove")) {
                for (String id : plugin.getConfigManager().getGradients().keySet()) {
                    if (id.toLowerCase().startsWith(args[2].toLowerCase())) {
                        completions.add(id);
                    }
                }
            }
            return completions;
        }
        
        if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
            String id = args[2];
            GradientConfig gradient = plugin.getConfigManager().getGradient(id);
            if (gradient != null) {
                for (int i = 1; i <= gradient.getSlots(); i++) {
                    if (String.valueOf(i).startsWith(args[3])) {
                        completions.add(String.valueOf(i));
                    }
                }
            }
            return completions;
        }
        
        if (args.length == 5 && args[1].equalsIgnoreCase("set")) {
            List<String> colorExamples = Arrays.asList(
                "#FF5555", "#55FF55", "#5555FF", "#FFFF55", "#FF55FF", "#55FFFF",
                "#FFFFFF", "#000000", "#FFAA00", "#AA00FF", "&c", "&a", "&6", "red", "blue"
            );
            for (String example : colorExamples) {
                if (example.toLowerCase().startsWith(args[4].toLowerCase())) {
                    completions.add(example);
                }
            }
            return completions;
        }
        
        if (args.length >= 6 && args[1].equalsIgnoreCase("set")) {
            boolean hasPlayer = false;
            for (int i = 5; i < args.length; i++) {
                if (!args[i].equalsIgnoreCase("-s") && !args[i].isEmpty()) {
                    hasPlayer = true;
                    break;
                }
            }
            
            if (!hasPlayer && sender.hasPermission("qqcm.gradient.set.other")) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                        completions.add(online.getName());
                    }
                }
            }
            
            boolean hasSilent = false;
            for (String arg : args) {
                if (arg.equalsIgnoreCase("-s")) {
                    hasSilent = true;
                    break;
                }
            }
            if (!hasSilent && "-s".startsWith(args[args.length - 1].toLowerCase())) {
                completions.add("-s");
            }
            
            return completions;
        }
        
        return completions;
    }
}
