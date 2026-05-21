package com.allfire.qqcolormanager.storage;

import com.allfire.qqcolormanager.QQColorManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class YAMLStorage implements ColorStorage {
    private final QQColorManager plugin;
    private File dataFile;
    private YamlConfiguration data;

    public YAMLStorage(QQColorManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() throws Exception {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public void close() {
        save();
    }

    private void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save data.yml: " + e.getMessage());
        }
    }

    private String getPath(UUID uuid) {
        return "players." + uuid.toString();
    }

    @Override
    public void updatePlayerName(UUID uuid, String name) {
        data.set(getPath(uuid) + ".name", name);
        save();
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return data.getString(getPath(uuid) + ".name");
    }

    @Override
    public void setColor(UUID uuid, String playerName, String template, int slot, String hex) {
        updatePlayerName(uuid, playerName);
        data.set(getPath(uuid) + ".colors." + template + "." + slot, hex);
        save();
    }

    @Override
    public String getColor(UUID uuid, String template, int slot) {
        return data.getString(getPath(uuid) + ".colors." + template + "." + slot);
    }

    @Override
    public Map<Integer, String> getAllColors(UUID uuid, String template) {
        Map<Integer, String> result = new HashMap<>();
        var section = data.getConfigurationSection(getPath(uuid) + ".colors." + template);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                result.put(Integer.parseInt(key), section.getString(key));
            }
        }
        return result;
    }

    @Override
    public Map<String, Map<Integer, String>> getAllPlayerColors(UUID uuid) {
        Map<String, Map<Integer, String>> result = new HashMap<>();
        var colorsSection = data.getConfigurationSection(getPath(uuid) + ".colors");
        if (colorsSection != null) {
            for (String template : colorsSection.getKeys(false)) {
                Map<Integer, String> slots = new HashMap<>();
                var templateSection = colorsSection.getConfigurationSection(template);
                if (templateSection != null) {
                    for (String slot : templateSection.getKeys(false)) {
                        slots.put(Integer.parseInt(slot), templateSection.getString(slot));
                    }
                }
                result.put(template, slots);
            }
        }
        return result;
    }

    @Override
    public void removeColor(UUID uuid, String template, int slot) {
        data.set(getPath(uuid) + ".colors." + template + "." + slot, null);
        save();
    }

    @Override
    public void removeAllColors(UUID uuid) {
        data.set(getPath(uuid) + ".colors", null);
        save();
    }

    @Override
    public void setGradientColor(UUID uuid, String playerName, String gradient, int slot, String hex) {
        updatePlayerName(uuid, playerName);
        data.set(getPath(uuid) + ".gradients." + gradient + "." + slot, hex);
        save();
    }

    @Override
    public String getGradientColor(UUID uuid, String gradient, int slot) {
        return data.getString(getPath(uuid) + ".gradients." + gradient + "." + slot);
    }

    @Override
    public Map<Integer, String> getAllGradientColors(UUID uuid, String gradient) {
        Map<Integer, String> result = new HashMap<>();
        var section = data.getConfigurationSection(getPath(uuid) + ".gradients." + gradient);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                result.put(Integer.parseInt(key), section.getString(key));
            }
        }
        return result;
    }

    @Override
    public Map<String, Map<Integer, String>> getAllPlayerGradients(UUID uuid) {
        Map<String, Map<Integer, String>> result = new HashMap<>();
        var gradientsSection = data.getConfigurationSection(getPath(uuid) + ".gradients");
        if (gradientsSection != null) {
            for (String gradient : gradientsSection.getKeys(false)) {
                Map<Integer, String> slots = new HashMap<>();
                var gradientSection = gradientsSection.getConfigurationSection(gradient);
                if (gradientSection != null) {
                    for (String slot : gradientSection.getKeys(false)) {
                        slots.put(Integer.parseInt(slot), gradientSection.getString(slot));
                    }
                }
                result.put(gradient, slots);
            }
        }
        return result;
    }

    @Override
    public void removeGradient(UUID uuid, String gradient) {
        data.set(getPath(uuid) + ".gradients." + gradient, null);
        save();
    }

    @Override
    public void removeAllGradients(UUID uuid) {
        data.set(getPath(uuid) + ".gradients", null);
        save();
    }

    @Override
    public void clearPlayer(UUID uuid) {
        data.set(getPath(uuid), null);
        save();
    }

    @Override
    public boolean hasAnyData(UUID uuid) {
        return data.contains(getPath(uuid));
    }
}
