package com.allfire.qqcolormanager.placeholder;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.config.GradientConfig;
import com.allfire.qqcolormanager.config.TemplateConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public boolean canRegister() {
        return true;
    }
    
    @Override
    public @Nullable List<String> getPlaceholders() {
        List<String> placeholders = new ArrayList<>();
        
        for (String id : plugin.getConfigManager().getColors().keySet()) {
            TemplateConfig template = plugin.getConfigManager().getColor(id);
            if (template != null) {
                for (int slot = 1; slot <= template.getSlots(); slot++) {
                    placeholders.add("%qqcm_color_" + id + "_" + slot + "_fallback%");
                    placeholders.add("%qqcm_color_" + id + "_" + slot + "_&7%");
                    placeholders.add("%qqcm_color_" + id + "_" + slot + "_&f%");
                }
            }
        }
        
        for (String id : plugin.getConfigManager().getGradients().keySet()) {
            placeholders.add("%qqcm_gradient_" + id + "_start_fallback%");
            placeholders.add("%qqcm_gradient_" + id + "_start_&7%");
            placeholders.add("%qqcm_gradient_" + id + "_end_fallback%");
            placeholders.add("%qqcm_gradient_" + id + "_end_&7%");
        }
        
        return placeholders;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        
        String[] parts = params.split("_", 4);
        
        if (parts.length < 2) return "";
        
        String type = parts[0];
        
        if (type.equals("color") && parts.length >= 3) {
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
        
        return format.replace("$1", hex);
    }
    
    private String getGradientPlaceholder(Player player, String gradientId, String part, String fallback) {
        GradientConfig gradient = plugin.getConfigManager().getGradient(gradientId);
        if (gradient == null) {
            return fallback;
        }
        
        UUID uuid = player.getUniqueId();
        
        boolean hasAnyColor = false;
        Map<Integer, String> colors = new HashMap<>();
        
        // Сначала собираем все установленные цвета
        for (int slot = 1; slot <= gradient.getSlots(); slot++) {
            String color = plugin.getStorage().getGradientColor(uuid, gradientId, slot);
            if (color != null) {
                hasAnyColor = true;
                colors.put(slot, color);
            }
        }
        
        // Затем заполняем пустые слоты fallback цветами с поддержкой $1
        for (int slot = 1; slot <= gradient.getSlots(); slot++) {
            if (!colors.containsKey(slot)) {
                String fallbackColor = gradient.getFallbackColor(slot);
                
                // Обработка динамического fallback: $1, $2 и т.д.
                if (fallbackColor != null && fallbackColor.startsWith("$") && fallbackColor.length() > 1) {
                    try {
                        int refSlot = Integer.parseInt(fallbackColor.substring(1));
                        if (colors.containsKey(refSlot)) {
                            fallbackColor = colors.get(refSlot);
                        } else {
                            // Если ссылается на пустой слот, берём его fallback
                            fallbackColor = gradient.getFallbackColor(refSlot);
                            if (fallbackColor != null && fallbackColor.startsWith("$")) {
                                fallbackColor = "FFFFFF";
                            }
                        }
                    } catch (NumberFormatException e) {
                        fallbackColor = "FFFFFF";
                    }
                }
                
                if (fallbackColor == null || fallbackColor.isEmpty()) {
                    fallbackColor = "FFFFFF";
                }
                
                colors.put(slot, fallbackColor);
            }
        }
        
        if (!hasAnyColor) {
            return fallback;
        }
        
        if (part.equalsIgnoreCase("start") || part.equalsIgnoreCase("1")) {
            String format = gradient.getFormatStart();
            for (int slot = 1; slot <= gradient.getSlots(); slot++) {
                String color = colors.get(slot);
                if (color != null) {
                    format = format.replace("$" + slot, color);
                    format = format.replace("#$" + slot, "#" + color);
                }
            }
            return format;
        } else if (part.equalsIgnoreCase("end") || part.equalsIgnoreCase("2")) {
            String format = gradient.getFormatEnd();
            for (int slot = 1; slot <= gradient.getSlots(); slot++) {
                String color = colors.get(slot);
                if (color != null) {
                    format = format.replace("$" + slot, color);
                    format = format.replace("#$" + slot, "#" + color);
                }
            }
            return format;
        }
        
        return fallback;
    }
}
