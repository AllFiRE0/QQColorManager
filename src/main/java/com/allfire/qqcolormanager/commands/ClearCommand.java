package com.allfire.qqcolormanager.commands;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClearCommand {
    
    private final QQColorManager plugin;

    public ClearCommand(QQColorManager plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        String playerName = null;
        boolean silent = false;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-s")) {
                silent = true;
            } else if (playerName == null) {
                playerName = args[i];
            }
        }

        UUID targetUuid;
        String targetName;

        if (playerName == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console must specify a player");
                return;
            }
            if (!sender.hasPermission("qqcm.clear")) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
                return;
            }
            Player p = (Player) sender;
            targetUuid = p.getUniqueId();
            targetName = p.getName();
        } else {
            if (!sender.hasPermission("qqcm.clear.other")) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("no_permission"), silent);
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            if (!target.hasPlayedBefore() && target.getName() == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("player_not_found"), silent);
                return;
            }
            targetUuid = target.getUniqueId();
            targetName = target.getName() != null ? target.getName() : playerName;
        }

        if (!plugin.getStorage().hasAnyData(targetUuid)) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("nothing_to_clear"), silent);
            return;
        }

        plugin.getStorage().clearPlayer(targetUuid);

        if (!silent) {
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("player_cleared")
                .replace("<player>", targetName));
        }
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2 && sender.hasPermission("qqcm.clear.other")) {
            String input = args[1].toLowerCase();
            List<String> players = new ArrayList<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase().startsWith(input)) {
                    players.add(online.getName());
                }
            }
            return players;
        }
        return new ArrayList<>();
    }
}
