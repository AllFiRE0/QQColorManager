package com.allfire.qqcolormanager.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class GradientConfig {
    
    private final String id;
    private final int slots;
    private final String formatStart;
    private final String formatEnd;
    private final Map<Integer, String> fallbackColors;
    private final String inputRegex;

    public GradientConfig(ConfigurationSection section, String id) {
        this.id = id;
        this.slots = section != null ? section.getInt("slots", 3) : 3;
        this.formatStart = section != null ? section.getString("format_start", "<gradient:#$1:#$2:#$3>") : "<gradient:#$1:#$2:#$3>";
        this.formatEnd = section != null ? section.getString("format_end", "</gradient>") : "</gradient>";
        this.inputRegex = section != null ? section.getString("input_regex") : null;
        
        this.fallbackColors = new HashMap<>();
        if (section != null) {
            ConfigurationSection fallbackSection = section.getConfigurationSection("fallback_colors");
            if (fallbackSection != null) {
                for (String key : fallbackSection.getKeys(false)) {
                    try {
                        int slot = Integer.parseInt(key);
                        fallbackColors.put(slot, fallbackSection.getString(key));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        
        // Ensure all slots have fallback colors
        for (int i = 1; i <= slots; i++) {
            if (!fallbackColors.containsKey(i)) {
                fallbackColors.put(i, "FFFFFF");
            }
        }
    }
    
    public String getId() {
        return id;
    }
    
    public int getSlots() {
        return slots;
    }
    
    public String getFormatStart() {
        return formatStart;
    }
    
    public String getFormatEnd() {
        return formatEnd;
    }
    
    public String getFallbackColor(int slot) {
        return fallbackColors.getOrDefault(slot, "FFFFFF");
    }
    
    public Map<Integer, String> getFallbackColors() {
        return fallbackColors;
    }
    
    public String getInputRegex() {
        return inputRegex;
    }
}
