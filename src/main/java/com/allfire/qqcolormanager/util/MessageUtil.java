package com.allfire.qqcolormanager.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    
    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        
        Component component = MINI_MESSAGE.deserialize(message);
        
        if (sender instanceof Player) {
            ((Player) sender).getAdventure().sendMessage(component);
        } else {
            // Console doesn't support MiniMessage fully, strip tags
            sender.sendMessage(message.replaceAll("<[^>]*>", ""));
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
