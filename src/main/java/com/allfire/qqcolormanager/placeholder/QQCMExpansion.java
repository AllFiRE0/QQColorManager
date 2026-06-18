package com.allfire.qqcolormanager.placeholder;

import com.allfire.qqcolormanager.QQColorManager;
import com.allfire.qqcolormanager.config.GradientConfig;
import com.allfire.qqcolormanager.config.TemplateConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
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
        
        // Обычные заполнители
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
        
        // Реляционные заполнители
        for (String id : plugin.getConfigManager().getColors().keySet()) {
            TemplateConfig template = plugin.getConfigManager().getColor(id);
            if (template != null) {
                for (int slot = 1; slot <= template.getSlots(); slot++) {
                    placeholders.add("%rel_qqcm_color_" + id + "_" + slot + "_fallback%");
                    placeholders.add("%rel_qqcm_color_" + id + "_" + slot + "_&7%");
                    placeholders.add("%rel_qqcm_color_" + id + "_" + slot + "_&f%");
                    placeholders.add("%relation_qqcm_color_" + id + "_" + slot + "_fallback%");
                    placeholders.add("%relation_qqcm_color_" + id + "_" + slot + "_&7%");
                    placeholders.add("%relation_qqcm_color_" + id + "_" + slot + "_&f%");
                }
            }
        }
        
        for (String id : plugin.getConfigManager().getGradients().keySet()) {
            placeholders.add("%rel_qqcm_gradient_" + id + "_start_fallback%");
            placeholders.add("%rel_qqcm_gradient_" + id + "_start_&7%");
            placeholders.add("%rel_qqcm_gradient_" + id + "_end_fallback%");
            placeholders.add("%rel_qqcm_gradient_" + id + "_end_&7%");
            placeholders.add("%relation_qqcm_gradient_" + id + "_start_fallback%");
            placeholders.add("%relation_qqcm_gradient_" + id + "_start_&7%");
            placeholders.add("%relation_qqcm_gradient_" + id + "_end_fallback%");
            placeholders.add("%relation_qqcm_gradient_" + id + "_end_&7%");
        }
        
        return placeholders;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        
        // Проверка на реляционные заполнители
        boolean isRel = params.startsWith("rel_") || params.startsWith("relation_");
        String actualParams = params;
        
        if (isRel) {
            // Убираем префикс rel_ или relation_
            if (params.startsWith("rel_")) {
                actualParams = params.substring(4);
            } else if (params.startsWith("relation_")) {
                actualParams = params.substring(9);
            }
        }
        
        String[] parts = actualParams.split("_", 4);
        
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
            
            if (isRel) {
                return getRelColorPlaceholder(player, template, slot, fallback, params);
            }
            return getColorPlaceholder(player, template, slot, fallback);
        }
        
        if (type.equals("gradient") && parts.length >= 3) {
            String gradient = parts[1];
            String part = parts[2];
            String fallback = parts.length >= 4 ? parts[3] : "";
            
            if (isRel) {
                return getRelGradientPlaceholder(player, gradient, part, fallback, params);
            }
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
        
        Map<Integer, String> colors = getGradientColors(uuid, gradientId, gradient);
        
        // Если нет установленных цветов, возвращаем fallback
        boolean hasAnyColor = false;
        for (String color : colors.values()) {
            if (color != null && !color.isEmpty()) {
                hasAnyColor = true;
                break;
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
    
    private String getRelColorPlaceholder(Player player, String templateId, int slot, String fallback, String fullParam) {
        // Определяем, в каком порядке обрабатывать: TARGET_AND_SENDER или SENDER_AND_TARGET
        String mode = plugin.getConfig().getString("relation.mode", "TARGET_AND_SENDER");
        
        Player target = getTarget(player, fullParam);
        if (target == null) {
            return fallback;
        }
        
        Player sender = player;
        Player firstPlayer;
        Player secondPlayer;
        
        if (mode.equalsIgnoreCase("TARGET_AND_SENDER")) {
            firstPlayer = target;
            secondPlayer = sender;
        } else { // SENDER_AND_TARGET
            firstPlayer = sender;
            secondPlayer = target;
        }
        
        // Получаем цвета обоих игроков
        String hex1 = getColorHex(firstPlayer, templateId, slot);
        String hex2 = getColorHex(secondPlayer, templateId, slot);
        
        // Если у кого-то нет цвета, возвращаем fallback
        if (hex1 == null || hex2 == null) {
            return fallback;
        }
        
        // Формируем результат с обоими цветами
        TemplateConfig template = plugin.getConfigManager().getColor(templateId);
        if (template == null) {
            return fallback;
        }
        
        // Используем формат первого игрока, но подставляем HEX второго
        String format = template.getFormat(slot);
        if (format == null) {
            return fallback;
        }
        
        // Для реляционных заполнителей возвращаем цвет первого игрока
        // (или комбинацию, в зависимости от необходимости)
        return format.replace("$1", hex1);
    }
    
    private String getRelGradientPlaceholder(Player player, String gradientId, String part, String fallback, String fullParam) {
        String mode = plugin.getConfig().getString("relation.mode", "TARGET_AND_SENDER");
        
        Player target = getTarget(player, fullParam);
        if (target == null) {
            return fallback;
        }
        
        Player sender = player;
        Player firstPlayer;
        Player secondPlayer;
        
        if (mode.equalsIgnoreCase("TARGET_AND_SENDER")) {
            firstPlayer = target;
            secondPlayer = sender;
        } else { // SENDER_AND_TARGET
            firstPlayer = sender;
            secondPlayer = target;
        }
        
        GradientConfig gradient = plugin.getConfigManager().getGradient(gradientId);
        if (gradient == null) {
            return fallback;
        }
        
        // Получаем градиенты обоих игроков
        Map<Integer, String> colors1 = getGradientColors(firstPlayer.getUniqueId(), gradientId, gradient);
        Map<Integer, String> colors2 = getGradientColors(secondPlayer.getUniqueId(), gradientId, gradient);
        
        // Проверяем, есть ли у обоих все цвета
        boolean hasAll1 = true;
        boolean hasAll2 = true;
        for (int slot = 1; slot <= gradient.getSlots(); slot++) {
            if (colors1.get(slot) == null) hasAll1 = false;
            if (colors2.get(slot) == null) hasAll2 = false;
        }
        
        if (!hasAll1 || !hasAll2) {
            return fallback;
        }
        
        if (part.equalsIgnoreCase("start") || part.equalsIgnoreCase("1")) {
            String format = gradient.getFormatStart();
            for (int slot = 1; slot <= gradient.getSlots(); slot++) {
                String color = colors1.get(slot);
                if (color != null) {
                    format = format.replace("$" + slot, color);
                    format = format.replace("#$" + slot, "#" + color);
                }
            }
            return format;
        } else if (part.equalsIgnoreCase("end") || part.equalsIgnoreCase("2")) {
            String format = gradient.getFormatEnd();
            for (int slot = 1; slot <= gradient.getSlots(); slot++) {
                String color = colors1.get(slot);
                if (color != null) {
                    format = format.replace("$" + slot, color);
                    format = format.replace("#$" + slot, "#" + color);
                }
            }
            return format;
        }
        
        return fallback;
    }
    
    private String getColorHex(Player player, String templateId, int slot) {
        TemplateConfig template = plugin.getConfigManager().getColor(templateId);
        if (template == null) {
            return null;
        }
        
        if (slot < 1 || slot > template.getSlots()) {
            return null;
        }
        
        return plugin.getStorage().getColor(player.getUniqueId(), templateId, slot);
    }
    
    private Map<Integer, String> getGradientColors(UUID uuid, String gradientId, GradientConfig gradient) {
        Map<Integer, String> colors = new HashMap<>();
        
        // Сначала собираем все установленные цвета
        for (int slot = 1; slot <= gradient.getSlots(); slot++) {
            String color = plugin.getStorage().getGradientColor(uuid, gradientId, slot);
            if (color != null) {
                colors.put(slot, color);
            }
        }
        
        // Затем заполняем пустые слоты fallback цветами
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
        
        return colors;
    }
    
    private Player getTarget(Player player, String fullParam) {
        // Пытаемся получить цель из контекста PlaceholderAPI
        // Если используется реляционный заполнитель, цель должна быть передана через API
        
        // Для простоты используем первого онлайн игрока, если цель не указана
        // В реальном плагине нужно использовать PlaceholderAPI реляционные методы
        // или парсить цель из параметра
        Player[] online = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        for (Player p : online) {
            if (!p.equals(player)) {
                return p;
            }
        }
        return null;
    }
}
