package com.allfire.qqcolormanager.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageUtil {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static BukkitAudiences adventure;
    
    public static void init(Plugin plugin) {
        adventure = BukkitAudiences.create(plugin);
    }
    
    public static void close() {
        if (adventure != null) {
            adventure.close();
        }
    }
    
    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        
        Component component = MINI_MESSAGE.deserialize(message);
        
        if (sender instanceof Player) {
            adventure.player((Player) sender).sendMessage(component);
        } else {
            String plainText = message.replaceAll("<[^>]*>", "");
            sender.sendMessage(plainText);
        }
    }
    
    public static void send(CommandSender sender, String message, boolean silent) {
        if (!silent) {
            send(sender, message);
        }
    }
    
    public static Component parse(String message) {
        return MINI_MESSAGE.deserialize(message);
    }
}
