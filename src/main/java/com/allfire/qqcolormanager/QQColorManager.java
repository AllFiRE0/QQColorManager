package com.allfire.qqcolormanager;

import com.allfire.qqcolormanager.commands.QQCMCommand;
import com.allfire.qqcolormanager.config.ConfigManager;
import com.allfire.qqcolormanager.placeholder.QQCMExpansion;
import com.allfire.qqcolormanager.storage.ColorStorage;
import com.allfire.qqcolormanager.storage.H2Storage;
import com.allfire.qqcolormanager.storage.MySQLStorage;
import com.allfire.qqcolormanager.storage.YAMLStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class QQColorManager extends JavaPlugin {
    private static QQColorManager instance;
    private ConfigManager configManager;
    private ColorStorage storage;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        configManager.load();
        
        initStorage();
        
        QQCMCommand command = new QQCMCommand(this);
        getCommand("qqcm").setExecutor(command);
        getCommand("qqcm").setTabCompleter(command);
        
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new QQCMExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered");
        } else {
            getLogger().warning("PlaceholderAPI not found - placeholders will not work");
        }
        
        getLogger().info("QQColorManager v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.close();
        }
        getLogger().info("QQColorManager disabled");
    }

    private void initStorage() {
        String type = getConfig().getString("database.type", "h2").toLowerCase();
        
        try {
            switch (type) {
                case "mysql":
                    storage = new MySQLStorage(getConfig().getConfigurationSection("database.mysql"));
                    break;
                case "yaml":
                    storage = new YAMLStorage(this);
                    break;
                case "h2":
                default:
                    storage = new H2Storage(getConfig().getConfigurationSection("database.h2"), this);
                    break;
            }
            storage.init();
            getLogger().info("Storage initialized: " + type);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize storage: " + type, e);
            storage = new YAMLStorage(this);
            try {
                storage.init();
                getLogger().warning("Falling back to YAML storage");
            } catch (Exception ex) {
                getLogger().log(Level.SEVERE, "Failed to initialize fallback storage", ex);
            }
        }
    }

    public static QQColorManager getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ColorStorage getStorage() {
        return storage;
    }
}
