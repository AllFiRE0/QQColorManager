package com.allfire.qqcolormanager.commands;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.util.MessageUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class InfoCommand {
    private final QQColorManager plugin;

    public InfoCommand(QQColorManager plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        // /qqcm info <player> [page] [-s]
        if (args.length < 2) {
            MessageUtil.send(sender, "<red>Usage: /qqcm info <player> [page] [-s]");
            return;
        }

        String playerName = args[1];
        int page = 1;
        boolean silent = false;

        for (int i = 2; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-s")) {
                silent = true;
            } else {
                try {
                    page = Integer.parseInt(args[i]);
                } catch (NumberFormatException ignored) {}
            }
        }

        // Check permissions
        if (!playerName.equalsIgnoreCase(sender.getName()) && !sender.hasPermission("qqcm.info.other")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        if (playerName.equalsIgnoreCase(sender.getName()) && !sender.hasPermission("qqcm.info")) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
            return;
        }

        // Get target player
        OfflinePlayer target;
        if (Bukkit.getPlayerExact(playerName) != null) {
            target = Bukkit.getPlayerExact(playerName);
        } else {
            target = Bukkit.getOfflinePlayer(playerName);
            if (!target.hasPlayedBefore() && target.getName() == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("player_not_found"), silent);
                return;
            }
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : playerName;

        // Get all data
        Map<String, Map<Integer, String>> colors = plugin.getStorage().getAllPlayerColors(targetUuid);
        Map<String, Map<Integer, String>> gradients = plugin.getStorage().getAllPlayerGradients(targetUuid);

        if (colors.isEmpty() && gradients.isEmpty()) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_colors"), silent);
            return;
        }

        // Build lines for pagination
        List<String> lines = new ArrayList<>();
        lines.add("<green>=== QQCM инфо: " + displayName + " ===");

        if (!colors.isEmpty()) {
            lines.add("<yellow>[Цвета]");
            for (Map.Entry<String, Map<Integer, String>> entry : colors.entrySet()) {
                String templateId = entry.getKey();
                Map<Integer, String> slots = entry.getValue();
                lines.add("  <white>" + templateId + ":");
                for (int slot = 1; slot <= 10; slot++) { // Max 10 slots
                    if (slots.containsKey(slot)) {
                        lines.add("    <gray>Слот " + slot + ": <white>#" + slots.get(slot));
                    } else if (plugin.getConfigManager().getColor(templateId) != null && 
                               slot <= plugin.getConfigManager().getColor(templateId).getSlots()) {
                        lines.add("    <gray>Слот " + slot + ": <dark_gray>не установлен");
                    }
                }
            }
        }

        if (!gradients.isEmpty()) {
            lines.add("<yellow>[Градиенты]");
            for (Map.Entry<String, Map<Integer, String>> entry : gradients.entrySet()) {
                String gradientId = entry.getKey();
                Map<Integer, String> slots = entry.getValue();
                lines.add("  <white>" + gradientId + ":");
                for (int slot = 1; slot <= plugin.getConfigManager().getGradient(gradientId).getSlots(); slot++) {
                    if (slots.containsKey(slot)) {
                        lines.add("    <gray>Слот " + slot + ": <white>#" + slots.get(slot));
                    } else {
                        String fallback = plugin.getConfigManager().getGradient(gradientId).getFallbackColor(slot);
                        lines.add("    <gray>Слот " + slot + ": <dark_gray>не установлен <gray>(fallback: #" + fallback + ")");
                    }
                }
            }
        }

        // Пагинация (6 строк на страницу, не считая заголовка)
        int linesPerPage = 6;
        int totalPages = (int) Math.ceil((double) (lines.size() - 1) / linesPerPage);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int startIdx = 1 + (page - 1) * linesPerPage;
        int endIdx = Math.min(startIdx + linesPerPage, lines.size());

        for (int i = startIdx; i < endIdx; i++) {
            MessageUtil.send(sender, lines.get(i));
        }

        if (!silent && totalPages > 0) {
            sendPaginationButtons(sender, playerName, page, totalPages);
        }
    }

    private void sendPaginationButtons(CommandSender sender, String playerName, int currentPage, int totalPages) {
        if (!(sender instanceof Player)) return;
        
        Player player = (Player) sender;
        TextComponent message = new TextComponent();
        
        // Назад
        TextComponent back = new TextComponent("[← Назад] ");
        if (currentPage > 1) {
            back.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/qqcm info " + playerName + " " + (currentPage - 1)));
            back.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                new ComponentBuilder("Предыдущая страница").create()));
            back.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        } else {
            back.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        }
        
        // Страница
        TextComponent page = new TextComponent("Страница: " + currentPage + "/" + totalPages + " ");
        page.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        
        // Далее
        TextComponent next = new TextComponent("[Далее →]");
        if (currentPage < totalPages) {
            next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/qqcm info " + playerName + " " + (currentPage + 1)));
            next.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                new ComponentBuilder("Следующая страница").create()));
            next.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        } else {
            next.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        }
        
        message.addExtra(back);
        message.addExtra(page);
        message.addExtra(next);
        
        player.spigot().sendMessage(message);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            // Подсказка игроков
            String input = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
