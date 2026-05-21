package com.allfire.qqcolormanager.commands;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.config.TemplateConfig;
import com.allfire.qqcolormanager.util.ColorUtil;
import com.allfire.qqcolormanager.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ColorCommand {
    private final QQColorManager plugin;

    public ColorCommand(QQColorManager plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(sender, "<red>Usage: /qqcm color <set|get|remove> ...");
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
                MessageUtil.send(sender, "<red>Unknown color action: " + action);
        }
    }

    private void handleSet(CommandSender sender, String[] args) {
        // /qqcm color set <id> <slot> <color> [player] [-s]
        if (args.length < 5) {
            MessageUtil.send(sender, "<red>Usage: /qqcm color set <id> <slot> <color> [player] [-s]");
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

        // Check permissions
        if (playerName != null && !sender.hasPermission("qqcm.color.set.other")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }
        if (playerName == null && !(sender instanceof Player) && !silent) {
            sender.sendMessage("Console must specify a player");
            return;
        }
        if (playerName == null && !sender.hasPermission("qqcm.color.set")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        // Get template config
        TemplateConfig template = plugin.getConfigManager().getColor(id);
        if (template == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("template_not_found"), silent);
            return;
        }

        if (slot < 1 || slot > template.getSlots()) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("slot_not_found"), silent);
            return;
        }

        // Parse color
        String hex = ColorUtil.extractHex(colorInput);
        if (hex == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid_color"), silent);
            return;
        }

        // Validate with regex if present
        String regex = template.getInputRegex();
        if (regex != null && !Pattern.matches(regex, hex)) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid_regex")
                .replace("<regex>", regex), silent);
            return;
        }

        // Get target player
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

        // Save color
        String oldColor = plugin.getStorage().getColor(targetUuid, id, slot);
        plugin.getStorage().setColor(targetUuid, targetName, id, slot, hex);

        if (!silent) {
            if (oldColor == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("color_set")
                    .replace("<color>", "#" + hex));
            } else {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("color_updated")
                    .replace("<old_color>", "#" + oldColor)
                    .replace("<new_color>", "#" + hex));
            }
        }
    }

    private void handleGet(CommandSender sender, String[] args) {
        // /qqcm color get <id> [player] [-s]
        if (args.length < 3) {
            MessageUtil.send(sender, "<red>Usage: /qqcm color get <id> [player] [-s]");
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

        // Check permissions
        if (playerName != null && !sender.hasPermission("qqcm.color.get.other")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }
        if (playerName == null && !(sender instanceof Player)) {
            sender.sendMessage("Console must specify a player");
            return;
        }
        if (playerName == null && !sender.hasPermission("qqcm.color.get")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        // Get template
        TemplateConfig template = plugin.getConfigManager().getColor(id);
        if (template == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("template_not_found"), silent);
            return;
        }

        // Get target
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
            MessageUtil.send(sender, "<green>=== Color: " + id + " ===");
            for (int slot = 1; slot <= template.getSlots(); slot++) {
                String color = plugin.getStorage().getColor(targetUuid, id, slot);
                if (color != null) {
                    MessageUtil.send(sender, "  <yellow>Slot " + slot + ": <white>#" + color);
                } else {
                    MessageUtil.send(sender, "  <gray>Slot " + slot + ": <dark_gray>not set");
                }
            }
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        // /qqcm color remove <id> <slot> [player] [-s]
        if (args.length < 4) {
            MessageUtil.send(sender, "<red>Usage: /qqcm color remove <id> <slot> [player] [-s]");
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

        String playerName = null;
        boolean silent = false;

        for (int i = 4; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-s")) {
                silent = true;
            } else if (playerName == null) {
                playerName = args[i];
            }
        }

        // Check permissions
        if (playerName != null && !sender.hasPermission("qqcm.color.remove.other")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }
        if (playerName == null && !(sender instanceof Player)) {
            sender.sendMessage("Console must specify a player");
            return;
        }
        if (playerName == null && !sender.hasPermission("qqcm.color.remove")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        // Get template
        TemplateConfig template = plugin.getConfigManager().getColor(id);
        if (template == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("template_not_found"), silent);
            return;
        }

        if (slot < 1 || slot > template.getSlots()) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("slot_not_found"), silent);
            return;
        }

        // Get target
        UUID targetUuid;
        String targetDisplayName;
        if (playerName == null) {
            targetUuid = ((Player) sender).getUniqueId();
            targetDisplayName = sender.getName();
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (!target.hasPlayedBefore() && target.getName() == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("player_not_found"), silent);
                return;
            }
            targetUuid = target.getUniqueId();
            targetDisplayName = target.getName() != null ? target.getName() : playerName;
        }

        String existing = plugin.getStorage().getColor(targetUuid, id, slot);
        if (existing == null) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("nothing_to_remove"), silent);
            return;
        }

        plugin.getStorage().removeColor(targetUuid, id, slot);

        if (!silent) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("color_removed")
                .replace("<template>", id)
                .replace("<slot>", String.valueOf(slot)));
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 2) {
            completions.addAll(Arrays.asList("set", "get", "remove"));
        } else if (args.length == 3 && (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("remove"))) {
            completions.addAll(plugin.getConfigManager().getColors().keySet());
        } else if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
            String id = args[2];
            TemplateConfig template = plugin.getConfigManager().getColor(id);
            if (template != null) {
                for (int i = 1; i <= template.getSlots(); i++) {
                    completions.add(String.valueOf(i));
                }
            }
        } else if (args.length == 4 && args[1].equalsIgnoreCase("remove")) {
            String id = args[2];
            TemplateConfig template = plugin.getConfigManager().getColor(id);
            if (template != null) {
                for (int i = 1; i <= template.getSlots(); i++) {
                    completions.add(String.valueOf(i));
                }
            }
        }
        
        return completions;
    }
}
