package com.allfire.qqcolormanager.config;

import com.allfire.qqcolormanager.QQColorManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final QQColorManager plugin;
    private Map<String, TemplateConfig> colors;
    private Map<String, GradientConfig> gradients;
    private Map<String, String> messages;

    public ConfigManager(QQColorManager plugin) {
        this.plugin = plugin;
        this.colors = new HashMap<>();
        this.gradients = new HashMap<>();
        this.messages = new HashMap<>();
    }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        
        // Load colors
        colors.clear();
        ConfigurationSection colorsSection = config.getConfigurationSection("colors");
        if (colorsSection != null) {
            for (String id : colorsSection.getKeys(false)) {
                colors.put(id, new TemplateConfig(colorsSection.getConfigurationSection(id), id));
            }
        }
        
        // Load gradients
        gradients.clear();
        ConfigurationSection gradientsSection = config.getConfigurationSection("gradients");
        if (gradientsSection != null) {
            for (String id : gradientsSection.getKeys(false)) {
                gradients.put(id, new GradientConfig(gradientsSection.getConfigurationSection(id), id));
            }
        }
        
        // Load messages
        messages.clear();
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                messages.put(key, messagesSection.getString(key));
            }
        }
    }
    
    public void reload() {
        load();
    }
    
    public Map<String, TemplateConfig> getColors() {
        return colors;
    }
    
    public Map<String, GradientConfig> getGradients() {
        return gradients;
    }
    
    public TemplateConfig getColor(String id) {
        return colors.get(id);
    }
    
    public GradientConfig getGradient(String id) {
        return gradients.get(id);
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "<red>Message not found: " + key);
    }
}
