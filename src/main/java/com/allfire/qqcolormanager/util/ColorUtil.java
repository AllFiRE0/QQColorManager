package com.allfire.qqcolormanager.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    
    private static final Map<String, String> NAMED_COLORS = new HashMap<>();
    private static final Map<Character, String> VANILLA_TO_HEX = new HashMap<>();
    
    static {
        // ==================== HTML Color Names (140 цветов) ====================
        
        // Red
        NAMED_COLORS.put("indianred", "CD5C5C");
        NAMED_COLORS.put("lightcoral", "F08080");
        NAMED_COLORS.put("salmon", "FA8072");
        NAMED_COLORS.put("darksalmon", "E9967A");
        NAMED_COLORS.put("lightsalmon", "FFA07A");
        NAMED_COLORS.put("crimson", "DC143C");
        NAMED_COLORS.put("red", "FF0000");
        NAMED_COLORS.put("firebrick", "B22222");
        NAMED_COLORS.put("darkred", "8B0000");
        
        // Pink
        NAMED_COLORS.put("pink", "FFC0CB");
        NAMED_COLORS.put("lightpink", "FFB6C1");
        NAMED_COLORS.put("hotpink", "FF69B4");
        NAMED_COLORS.put("deeppink", "FF1493");
        NAMED_COLORS.put("mediumvioletred", "C71585");
        NAMED_COLORS.put("palevioletred", "DB7093");
        
        // Orange
        NAMED_COLORS.put("coral", "FF7F50");
        NAMED_COLORS.put("tomato", "FF6347");
        NAMED_COLORS.put("orangered", "FF4500");
        NAMED_COLORS.put("darkorange", "FF8C00");
        NAMED_COLORS.put("orange", "FFA500");
        
        // Yellow
        NAMED_COLORS.put("gold", "FFD700");
        NAMED_COLORS.put("yellow", "FFFF00");
        NAMED_COLORS.put("lightyellow", "FFFFE0");
        NAMED_COLORS.put("lemonchiffon", "FFFACD");
        NAMED_COLORS.put("lightgoldenrodyellow", "FAFAD2");
        NAMED_COLORS.put("papayawhip", "FFEFD5");
        NAMED_COLORS.put("moccasin", "FFE4B5");
        NAMED_COLORS.put("peachpuff", "FFDAB9");
        NAMED_COLORS.put("palegoldenrod", "EEE8AA");
        NAMED_COLORS.put("khaki", "F0E68C");
        NAMED_COLORS.put("darkkhaki", "BDB76B");
        
        // Purple
        NAMED_COLORS.put("lavender", "E6E6FA");
        NAMED_COLORS.put("thistle", "D8BFD8");
        NAMED_COLORS.put("plum", "DDA0DD");
        NAMED_COLORS.put("violet", "EE82EE");
        NAMED_COLORS.put("orchid", "DA70D6");
        NAMED_COLORS.put("fuchsia", "FF00FF");
        NAMED_COLORS.put("magenta", "FF00FF");
        NAMED_COLORS.put("mediumorchid", "BA55D3");
        NAMED_COLORS.put("mediumpurple", "9370DB");
        NAMED_COLORS.put("rebeccapurple", "663399");
        NAMED_COLORS.put("blueviolet", "8A2BE2");
        NAMED_COLORS.put("darkviolet", "9400D3");
        NAMED_COLORS.put("darkorchid", "9932CC");
        NAMED_COLORS.put("darkmagenta", "8B008B");
        NAMED_COLORS.put("purple", "800080");
        NAMED_COLORS.put("indigo", "4B0082");
        NAMED_COLORS.put("slateblue", "6A5ACD");
        NAMED_COLORS.put("darkslateblue", "483D8B");
        NAMED_COLORS.put("mediumslateblue", "7B68EE");
        
        // Green
        NAMED_COLORS.put("greenyellow", "ADFF2F");
        NAMED_COLORS.put("chartreuse", "7FFF00");
        NAMED_COLORS.put("lawngreen", "7CFC00");
        NAMED_COLORS.put("lime", "00FF00");
        NAMED_COLORS.put("limegreen", "32CD32");
        NAMED_COLORS.put("palegreen", "98FB98");
        NAMED_COLORS.put("lightgreen", "90EE90");
        NAMED_COLORS.put("mediumspringgreen", "00FA9A");
        NAMED_COLORS.put("springgreen", "00FF7F");
        NAMED_COLORS.put("mediumseagreen", "3CB371");
        NAMED_COLORS.put("seagreen", "2E8B57");
        NAMED_COLORS.put("forestgreen", "228B22");
        NAMED_COLORS.put("green", "008000");
        NAMED_COLORS.put("darkgreen", "006400");
        NAMED_COLORS.put("yellowgreen", "9ACD32");
        NAMED_COLORS.put("olivedrab", "6B8E23");
        NAMED_COLORS.put("olive", "808000");
        NAMED_COLORS.put("darkolivegreen", "556B2F");
        NAMED_COLORS.put("mediumaquamarine", "66CDAA");
        NAMED_COLORS.put("darkseagreen", "8FBC8F");
        NAMED_COLORS.put("lightseagreen", "20B2AA");
        NAMED_COLORS.put("darkcyan", "008B8B");
        NAMED_COLORS.put("teal", "008080");
        
        // Blue
        NAMED_COLORS.put("aqua", "00FFFF");
        NAMED_COLORS.put("cyan", "00FFFF");
        NAMED_COLORS.put("lightcyan", "E0FFFF");
        NAMED_COLORS.put("paleturquoise", "AFEEEE");
        NAMED_COLORS.put("aquamarine", "7FFFD4");
        NAMED_COLORS.put("turquoise", "40E0D0");
        NAMED_COLORS.put("mediumturquoise", "48D1CC");
        NAMED_COLORS.put("darkturquoise", "00CED1");
        NAMED_COLORS.put("cadetblue", "5F9EA0");
        NAMED_COLORS.put("steelblue", "4682B4");
        NAMED_COLORS.put("lightsteelblue", "B0C4DE");
        NAMED_COLORS.put("powderblue", "B0E0E6");
        NAMED_COLORS.put("lightblue", "ADD8E6");
        NAMED_COLORS.put("skyblue", "87CEEB");
        NAMED_COLORS.put("lightskyblue", "87CEFA");
        NAMED_COLORS.put("deepskyblue", "00BFFF");
        NAMED_COLORS.put("dodgerblue", "1E90FF");
        NAMED_COLORS.put("cornflowerblue", "6495ED");
        NAMED_COLORS.put("royalblue", "4169E1");
        NAMED_COLORS.put("blue", "0000FF");
        NAMED_COLORS.put("mediumblue", "0000CD");
        NAMED_COLORS.put("darkblue", "00008B");
        NAMED_COLORS.put("navy", "000080");
        NAMED_COLORS.put("midnightblue", "191970");
        
        // Brown
        NAMED_COLORS.put("cornsilk", "FFF8DC");
        NAMED_COLORS.put("blanchedalmond", "FFEBCD");
        NAMED_COLORS.put("bisque", "FFE4C4");
        NAMED_COLORS.put("navajowhite", "FFDEAD");
        NAMED_COLORS.put("wheat", "F5DEB3");
        NAMED_COLORS.put("burlywood", "DEB887");
        NAMED_COLORS.put("tan", "D2B48C");
        NAMED_COLORS.put("rosybrown", "BC8F8F");
        NAMED_COLORS.put("sandybrown", "F4A460");
        NAMED_COLORS.put("goldenrod", "DAA520");
        NAMED_COLORS.put("darkgoldenrod", "B8860B");
        NAMED_COLORS.put("peru", "CD853F");
        NAMED_COLORS.put("chocolate", "D2691E");
        NAMED_COLORS.put("saddlebrown", "8B4513");
        NAMED_COLORS.put("sienna", "A0522D");
        NAMED_COLORS.put("brown", "A52A2A");
        NAMED_COLORS.put("maroon", "800000");
        
        // White
        NAMED_COLORS.put("white", "FFFFFF");
        NAMED_COLORS.put("snow", "FFFAFA");
        NAMED_COLORS.put("honeydew", "F0FFF0");
        NAMED_COLORS.put("mintcream", "F5FFFA");
        NAMED_COLORS.put("azure", "F0FFFF");
        NAMED_COLORS.put("aliceblue", "F0F8FF");
        NAMED_COLORS.put("ghostwhite", "F8F8FF");
        NAMED_COLORS.put("whitesmoke", "F5F5F5");
        NAMED_COLORS.put("seashell", "FFF5EE");
        NAMED_COLORS.put("beige", "F5F5DC");
        NAMED_COLORS.put("oldlace", "FDF5E6");
        NAMED_COLORS.put("floralwhite", "FFFAF0");
        NAMED_COLORS.put("ivory", "FFFFF0");
        NAMED_COLORS.put("antiquewhite", "FAEBD7");
        NAMED_COLORS.put("linen", "FAF0E6");
        NAMED_COLORS.put("lavenderblush", "FFF0F5");
        NAMED_COLORS.put("mistyrose", "FFE4E1");
        
        // Gray
        NAMED_COLORS.put("gainsboro", "DCDCDC");
        NAMED_COLORS.put("lightgray", "D3D3D3");
        NAMED_COLORS.put("silver", "C0C0C0");
        NAMED_COLORS.put("darkgray", "A9A9A9");
        NAMED_COLORS.put("gray", "808080");
        NAMED_COLORS.put("dimgray", "696969");
        NAMED_COLORS.put("lightslategray", "778899");
        NAMED_COLORS.put("slategray", "708090");
        NAMED_COLORS.put("darkslategray", "2F4F4F");
        NAMED_COLORS.put("black", "000000");
        
        // ==================== Minecraft стандартные цвета ====================
        NAMED_COLORS.put("dark_blue", "0000AA");
        NAMED_COLORS.put("dark_green", "00AA00");
        NAMED_COLORS.put("dark_aqua", "00AAAA");
        NAMED_COLORS.put("dark_red", "AA0000");
        NAMED_COLORS.put("dark_purple", "AA00AA");
        NAMED_COLORS.put("dark_gray", "555555");
        NAMED_COLORS.put("light_purple", "FF55FF");
        
        // Краткие названия (без подчёркиваний)
        NAMED_COLORS.put("darkblue", "0000AA");
        NAMED_COLORS.put("darkgreen", "00AA00");
        NAMED_COLORS.put("darkaqua", "00AAAA");
        NAMED_COLORS.put("darkred", "AA0000");
        NAMED_COLORS.put("darkpurple", "AA00AA");
        NAMED_COLORS.put("lightpurple", "FF55FF");
        
        // ==================== Vanilla коды ====================
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
        
        // 2. CMI HEX форматы: {#FF5555}, {#FF5555>}, {#FF5555<}
        Matcher cmiHexMatcher = Pattern.compile("\\{\\#([A-Fa-f0-9]{6})[><]?\\}").matcher(cleaned);
        if (cmiHexMatcher.find()) return cmiHexMatcher.group(1).toUpperCase();
        
        // 3. CMI названия цветов: {#lime}, {#red>}, {#blue<}
        Matcher cmiNamedMatcher = Pattern.compile("\\{\\#([a-zA-Z]+)[><]?\\}").matcher(cleaned);
        if (cmiNamedMatcher.find()) {
            String colorName = cmiNamedMatcher.group(1).toLowerCase();
            if (NAMED_COLORS.containsKey(colorName)) {
                return NAMED_COLORS.get(colorName);
            }
        }
        
        // 4. Квадратные скобки: [#FF5555], [#lime]
        Matcher bracketHexMatcher = Pattern.compile("\\[\\#([A-Fa-f0-9]{6})\\]").matcher(cleaned);
        if (bracketHexMatcher.find()) return bracketHexMatcher.group(1).toUpperCase();
        
        Matcher bracketNamedMatcher = Pattern.compile("\\[\\#([a-zA-Z]+)\\]").matcher(cleaned);
        if (bracketNamedMatcher.find()) {
            String colorName = bracketNamedMatcher.group(1).toLowerCase();
            if (NAMED_COLORS.containsKey(colorName)) {
                return NAMED_COLORS.get(colorName);
            }
        }
        
        // 5. Угловые скобки без #: <FFFFFF>, <lime>
        Matcher angleHexMatcher = Pattern.compile("<([A-Fa-f0-9]{6})>").matcher(cleaned);
        if (angleHexMatcher.find()) return angleHexMatcher.group(1).toUpperCase();
        
        Matcher angleNamedMatcher = Pattern.compile("<([a-zA-Z]+)>").matcher(cleaned);
        if (angleNamedMatcher.find()) {
            String colorName = angleNamedMatcher.group(1).toLowerCase();
            if (NAMED_COLORS.containsKey(colorName)) {
                return NAMED_COLORS.get(colorName);
            }
        }
        
        // 6. Просто #lime (решётка + название)
        Matcher hashNamedMatcher = Pattern.compile("#([a-zA-Z]+)").matcher(cleaned);
        if (hashNamedMatcher.find()) {
            String colorName = hashNamedMatcher.group(1).toLowerCase();
            if (NAMED_COLORS.containsKey(colorName)) {
                return NAMED_COLORS.get(colorName);
            }
        }
        
        // 7. Чистый HEX с # или без
        Matcher hexMatcher = Pattern.compile("^#?([A-Fa-f0-9]{6})$").matcher(cleaned);
        if (hexMatcher.matches()) return hexMatcher.group(1).toUpperCase();
        
        // 8. HEX внутри любого текста (последняя надежда)
        Matcher anyHex = Pattern.compile("([A-Fa-f0-9]{6})").matcher(cleaned);
        if (anyHex.find()) return anyHex.group(1).toUpperCase();
        
        // 9. Vanilla RGB: &x&F&F&F&F&F&F
        if (cleaned.matches("(?i)&x(&[0-9a-f]){6}")) {
            StringBuilder hex = new StringBuilder();
            Matcher m = Pattern.compile("(?i)&([0-9a-f])").matcher(cleaned);
            while (m.find()) hex.append(m.group(1));
            return hex.toString().toUpperCase();
        }
        
        // 10. Vanilla RGB с §: §x§F§F§F§F§F§F
        if (cleaned.matches("(?i)§x(§[0-9a-f]){6}")) {
            StringBuilder hex = new StringBuilder();
            Matcher m = Pattern.compile("(?i)§([0-9a-f])").matcher(cleaned);
            while (m.find()) hex.append(m.group(1));
            return hex.toString().toUpperCase();
        }
        
        // 11. Просто название цвета (без обёртки)
        String lowerInput = cleaned.toLowerCase();
        if (NAMED_COLORS.containsKey(lowerInput)) {
            return NAMED_COLORS.get(lowerInput);
        }
        
        // 12. Vanilla код &c
        if (cleaned.matches("^&[0-9a-f]$")) {
            return VANILLA_TO_HEX.get(cleaned.charAt(1));
        }
        
        // 13. Vanilla код с §
        if (cleaned.matches("^§[0-9a-f]$")) {
            return VANILLA_TO_HEX.get(cleaned.charAt(1));
        }
        
        return null;
    }
    
    public static boolean isValidHex(String hex) {
        return hex != null && hex.matches("^[A-Fa-f0-9]{6}$");
    }
}
