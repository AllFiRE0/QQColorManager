package com.allfire.qqcolormanager.storage;

import java.util.Map;
import java.util.UUID;

public interface ColorStorage {
    void init() throws Exception;
    void close();
    
    // Player name management
    void updatePlayerName(UUID uuid, String name);
    String getPlayerName(UUID uuid);
    
    // Colors
    void setColor(UUID uuid, String playerName, String template, int slot, String hex);
    String getColor(UUID uuid, String template, int slot);
    Map<Integer, String> getAllColors(UUID uuid, String template);
    Map<String, Map<Integer, String>> getAllPlayerColors(UUID uuid);
    void removeColor(UUID uuid, String template, int slot);
    void removeAllColors(UUID uuid);
    
    // Gradients
    void setGradientColor(UUID uuid, String playerName, String gradient, int slot, String hex);
    String getGradientColor(UUID uuid, String gradient, int slot);
    Map<Integer, String> getAllGradientColors(UUID uuid, String gradient);
    Map<String, Map<Integer, String>> getAllPlayerGradients(UUID uuid);
    void removeGradient(UUID uuid, String gradient);
    void removeAllGradients(UUID uuid);
    
    // Full clear
    void clearPlayer(UUID uuid);
    
    // Check if player has any data
    boolean hasAnyData(UUID uuid);
}
