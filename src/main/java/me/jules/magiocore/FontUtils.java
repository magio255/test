package me.jules.magiocore;

import java.util.HashMap;
import java.util.Map;

public class FontUtils {
    private static final Map<Character, Character> SMALL_CAPS = new HashMap<>();

    static {
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String smallCaps = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ0123456789";
        for (int i = 0; i < normal.length(); i++) {
            SMALL_CAPS.put(normal.charAt(i), smallCaps.charAt(i));
        }
    }

    public static String toSmallCaps(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            sb.append(SMALL_CAPS.getOrDefault(c, c));
        }
        return sb.toString();
    }
}
