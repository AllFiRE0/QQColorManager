package com.allfire.qqcolormanager.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    
    private static final Map<String, String> NAMED_COLORS = new HashMap<>();
    private static final Map<Character, String> VANILLA_TO_HEX = new HashMap<>();
    
    static {
        // Названия цветов
        NAMED_COLORS.put("black", "000000");
        NAMED_COLORS.put("dark_blue", "0000AA");
        NAMED_COLORS.put("dark_green", "00AA00");
        NAMED_COLORS.put("dark_aqua", "00AAAA");
        NAMED_COLORS.put("dark_red", "AA0000");
        NAMED_COLORS.put("dark_purple", "AA00AA");
        NAMED_COLORS.put("gold", "FFAA00");
        NAMED_COLORS.put("gray", "AAAAAA");
        NAMED_COLORS.put("dark_gray", "555555");
        NAMED_COLORS.put("blue", "5555FF");
        NAMED_COLORS.put("green", "55FF55");
        NAMED_COLORS.put("aqua", "55FFFF");
        NAMED_COLORS.put("red", "FF5555");
        NAMED_COLORS.put("light_purple", "FF55FF");
        NAMED_COLORS.put("yellow", "FFFF55");
        NAMED_COLORS.put("white", "FFFFFF");
        
        // Краткие названия
        NAMED_COLORS.put("black", "000000");
        NAMED_COLORS.put("darkblue", "0000AA");
        NAMED_COLORS.put("darkgreen", "00AA00");
        NAMED_COLORS.put("darkaqua", "00AAAA");
        NAMED_COLORS.put("darkred", "AA0000");
        NAMED_COLORS.put("darkpurple", "AA00AA");
        NAMED_COLORS.put("darkgray", "555555");
        NAMED_COLORS.put("blue", "5555FF");
        NAMED_COLORS.put("green", "55FF55");
        NAMED_COLORS.put("aqua", "55FFFF");
        NAMED_COLORS.put("red", "FF5555");
        NAMED_COLORS.put("lightpurple", "FF55FF");
        NAMED_COLORS.put("yellow", "FFFF55");
        
        // Vanilla коды
        VANILLA_TO_HEX.put('0', "000000");
        VANILLA_TO_HEX.put('1', "0000AA");
        VANILLA_TO_HEX.put('2', "00AA00");
        VANILLA_TO_HEX.put('3', "00AAAA");
        VANILLA_TO_HEX.put('4', "AA0000");
        VANILLA_TO_HEX.put('5', "AA00AA");
        VANILLA_TO_HEX.put('6', "FFAA00");
        VANILLA_TO_HEX.put('7', "AAAAAA");
        VANILLA_TO_HEX.put('8', "555555");
        VANILLA_TO_HEX.put('9', "5555FF");
        VANILLA_TO_HEX.put('a', "55FF55");
        VANILLA_TO_HEX.put('b', "55FFFF");
        VANILLA_TO_HEX.put('c', "FF5555");
        VANILLA_TO_HEX.put('d', "FF55FF");
        VANILLA_TO_HEX.put('e', "FFFF55");
        VANILLA_TO_HEX.put('f', "FFFFFF");
    }
    
    public static String extractHex(String input) {
        if (input == null || input.isEmpty()) return null;
        
        String cleaned = input.trim();
        
        // 1. MiniMessage: <color:#FF5555> или <gradient:#FF5555:...>
        Matcher mmMatcher = Pattern.compile("#([A-Fa-f0-9]{6})").matcher(cleaned);
        if (mmMatcher.find()) return mmMatcher.group(1).toUpperCase();
        
        // 2. CMI форматы: {#FF5555}, {#FF5555>}, {#FF5555<}
        Matcher cmiMatcher = Pattern.compile("\\{\\#([A-Fa-f0-9]{6})[><]?\\}").matcher(cleaned);
        if (cmiMatcher.find()) return cmiMatcher.group(1).toUpperCase();
        
        // 3. HEX внутри любого текста
        Matcher anyHex = Pattern.compile("([A-Fa-f0-9]{6})").matcher(cleaned);
        if (anyHex.find()) return anyHex.group(1).toUpperCase();
        
        // 4. Vanilla RGB: &x&F&F&F&F&F&F
        if (cleaned.matches("(?i)&x(&[0-9a-f]){6}")) {
            StringBuilder hex = new StringBuilder();
            Matcher m = Pattern.compile("(?i)&([0-9a-f])").matcher(cleaned);
            while (m.find()) hex.append(m.group(1));
            return hex.toString().toUpperCase();
        }
        
        // 5. Vanilla RGB с §: §x§F§F§F§F§F§F
        if (cleaned.matches("(?i)§x(§[0-9a-f]){6}")) {
            StringBuilder hex = new StringBuilder();
            Matcher m = Pattern.compile("(?i)§([0-9a-f])").matcher(cleaned);
            while (m.find()) hex.append(m.group(1));
            return hex.toString().toUpperCase();
        }
        
        // 6. Vanilla код &c
        if (cleaned.matches("^&[0-9a-f]$")) {
            return VANILLA_TO_HEX.get(cleaned.charAt(1));
        }
        
        // 7. Vanilla код с §
        if (cleaned.matches("^§[0-9a-f]$")) {
            return VANILLA_TO_HEX.get(cleaned.charAt(1));
        }
        
        // 8. Названия цветов
        String lowerInput = cleaned.toLowerCase();
        if (NAMED_COLORS.containsKey(lowerInput)) {
            return NAMED_COLORS.get(lowerInput);
        }
        
        return null;
    }
    
    public static boolean isValidHex(String hex) {
        return hex != null && hex.matches("^[A-Fa-f0-9]{6}$");
    }
}
