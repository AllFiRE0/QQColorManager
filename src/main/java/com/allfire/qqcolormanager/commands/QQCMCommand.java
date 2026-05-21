package com.allfire.qqcolormanager.commands;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QQCMCommand implements TabExecutor {
    private final QQColorManager plugin;
    private final ColorCommand colorCommand;
    private final GradientCommand gradientCommand;
    private final InfoCommand infoCommand;
    private final ClearCommand clearCommand;

    public QQCMCommand(QQColorManager plugin) {
        this.plugin = plugin;
        this.colorCommand = new ColorCommand(plugin);
        this.gradientCommand = new GradientCommand(plugin);
        this.infoCommand = new InfoCommand(plugin);
        this.clearCommand = new ClearCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "color":
                colorCommand.execute(sender, args);
                break;
            case "gradient":
                gradientCommand.execute(sender, args);
                break;
            case "info":
                infoCommand.execute(sender, args);
                break;
            case "clear":
                clearCommand.execute(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "version":
                handleVersion(sender);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("unknown_command"));
                break;
        }
        return true;
    }

    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("qqcm.list")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"));
            return;
        }

        var colors = plugin.getConfigManager().getColors();
        var gradients = plugin.getConfigManager().getGradients();

        MessageUtil.send(sender, "<green>=== QQColorManager Templates ===");
        MessageUtil.send(sender, "<yellow>Colors:");
        for (String id : colors.keySet()) {
            int slots = colors.get(id).getSlots();
            MessageUtil.send(sender, "  <white>" + id + " <gray>(slots: " + slots + ")");
        }
        MessageUtil.send(sender, "<yellow>Gradients:");
        for (String id : gradients.keySet()) {
            int slots = gradients.get(id).getSlots();
            MessageUtil.send(sender, "  <white>" + id + " <gray>(slots: " + slots + ")");
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("qqcm.reload")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"));
            return;
        }

        plugin.getConfigManager().reload();
        MessageUtil.send(sender, plugin.getConfigManager().getMessage("reloaded"));
    }

    private void handleVersion(CommandSender sender) {
        if (!sender.hasPermission("qqcm.version") && !sender.hasPermission("qqcm.use")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"));
            return;
        }

        MessageUtil.send(sender, "<green>QQColorManager v" + plugin.getDescription().getVersion() + 
            " <gray>by AllFiRE");
        MessageUtil.send(sender, "<gray>Paper 1.21.1+ | Java 21+");
    }

    private void sendHelp(CommandSender sender) {
        MessageUtil.send(sender, plugin.getConfigManager().getMessage("help_header"));
        MessageUtil.send(sender, "<gray>/qqcm color set <id> <slot> <color> [player] [-s] - <white>Set color");
        MessageUtil.send(sender, "<gray>/qqcm color get <id> [player] [-s] - <white>Get color");
        MessageUtil.send(sender, "<gray>/qqcm color remove <id> <slot> [player] [-s] - <white>Remove color");
        MessageUtil.send(sender, "<gray>/qqcm gradient set <id> <slot> <color> [player] [-s] - <white>Set gradient");
        MessageUtil.send(sender, "<gray>/qqcm gradient get <id> [player] [-s] - <white>Get gradient");
        MessageUtil.send(sender, "<gray>/qqcm gradient remove <id> [player] [-s] - <white>Remove gradient");
        MessageUtil.send(sender, "<gray>/qqcm info <player> [page] [-s] - <white>Show player info");
        MessageUtil.send(sender, "<gray>/qqcm clear [player] [-s] - <white>Clear all player data");
        MessageUtil.send(sender, "<gray>/qqcm list - <white>List templates");
        MessageUtil.send(sender, "<gray>/qqcm reload - <white>Reload config");
        MessageUtil.send(sender, "<gray>/qqcm version - <white>Show version");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList(
                "color", "gradient", "info", "clear", "list", "reload", "version", "help"));
            return completions.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length >= 2) {
            switch (args[0].toLowerCase()) {
                case "color":
                    return colorCommand.tabComplete(sender, args);
                case "gradient":
                    return gradientCommand.tabComplete(sender, args);
                case "info":
                    return infoCommand.tabComplete(sender, args);
                case "clear":
                    return clearCommand.tabComplete(sender, args);
            }
        }

        return new ArrayList<>();
    }
}
