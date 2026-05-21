package com.allfire.qqcolormanager.placeholder;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.config.GradientConfig;
import com.allfire.qqcolormanager.config.TemplateConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QQCMExpansion extends PlaceholderExpansion {
    private final QQColorManager plugin;

    public QQCMExpansion(QQColorManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "qqcm";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AllFiRE";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        
        // Format: color_<template>_<slot>_<fallback>
        // Or: gradient_<gradient>_start_<fallback> or gradient_<gradient>_end_<fallback>
        
        String[] parts = params.split("_", 4);
        
        if (parts.length < 2) return "";
        
        String type = parts[0];
        
        if (type.equals("color") && parts.length >= 3) {
            // color_<template>_<slot>_<fallback>
            String template = parts[1];
            int slot;
            try {
                slot = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                return "";
            }
            String fallback = parts.length >= 4 ? parts[3] : "";
            
            return getColorPlaceholder(player, template, slot, fallback);
        }
        
        if (type.equals("gradient") && parts.length >= 3) {
            // gradient_<gradient>_start_<fallback> or gradient_<gradient>_end_<fallback>
            String gradient = parts[1];
            String part = parts[2];
            String fallback = parts.length >= 4 ? parts[3] : "";
            
            return getGradientPlaceholder(player, gradient, part, fallback);
        }
        
        return "";
    }
    
    private String getColorPlaceholder(Player player, String templateId, int slot, String fallback) {
        TemplateConfig template = plugin.getConfigManager().getColor(templateId);
        if (template == null) {
            return fallback;
        }
        
        if (slot < 1 || slot > template.getSlots()) {
            return fallback;
        }
        
        String hex = plugin.getStorage().getColor(player.getUniqueId(), templateId, slot);
        if (hex == null) {
            return fallback;
        }
        
        String format = template.getFormat(slot);
        if (format == null) {
            return fallback;
        }
        
        // Replace $1 with hex (without #)
        return format.replace("$1", hex);
    }
    
    private String getGradientPlaceholder(Player player, String gradientId, String part, String fallback) {
        GradientConfig gradient = plugin.getConfigManager().getGradient(gradientId);
        if (gradient == null) {
            return fallback;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Check if player has any gradient colors set
        boolean hasAnyColor = false;
        Map<Integer, String> colors = new HashMap<>();
        for (int slot = 1; slot <= gradient.getSlots(); slot++) {
            String color = plugin.getStorage().getGradientColor(uuid, gradientId, slot);
            if (color != null) {
                hasAnyColor = true;
                colors.put(slot, color);
            } else {
                // Use fallback color from config
                colors.put(slot, gradient.getFallbackColor(slot));
            }
        }
        
        if (!hasAnyColor) {
            return fallback;
        }
        
        if (part.equalsIgnoreCase("start") || part.equalsIgnoreCase("1")) {
            String format = gradient.getFormatStart();
            for (int slot = 1; slot <= gradient.getSlots(); slot++) {
                String color = colors.get(slot);
                format = format.replace("$" + slot, color);
                format = format.replace("#$" + slot, "#" + color);
            }
            return format;
        } else if (part.equalsIgnoreCase("end") || part.equalsIgnoreCase("2")) {
            return gradient.getFormatEnd();
        }
        
        return fallback;
    }
}
