package com.allfire.qqcolormanager.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class TemplateConfig {
    
    private final String id;
    private final Map<Integer, String> formats;
    private final int slots;
    private final String inputRegex;

    public TemplateConfig(ConfigurationSection section, String id) {
        this.id = id;
        this.formats = new HashMap<>();
        
        int maxSlot = 0;
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key.matches("\\d+")) {
                    int slot = Integer.parseInt(key);
                    formats.put(slot, section.getString(key));
                    if (slot > maxSlot) maxSlot = slot;
                }
            }
            this.inputRegex = section.getString("input_regex");
        } else {
            this.inputRegex = null;
        }
        this.slots = maxSlot;
    }
    
    public String getId() {
        return id;
    }
    
    public String getFormat(int slot) {
        return formats.get(slot);
    }
    
    public Map<Integer, String> getFormats() {
        return formats;
    }
    
    public int getSlots() {
        return slots;
    }
    
    public String getInputRegex() {
        return inputRegex;
    }
}
