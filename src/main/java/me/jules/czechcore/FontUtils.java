package me.jules.czechcore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FontUtils {
    private static final Map<Character, Character> SMALL_CAPS = new HashMap<>();
    private static final Pattern CODE_PATTERN = Pattern.compile("(&#[A-Fa-f0-9]{6}|&[0-9a-fk-orA-FK-OR]|§[0-9a-fk-orA-FK-OR])");

    static {
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String smallCaps = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";

        for (int i = 0; i < normal.length(); i++) {
            SMALL_CAPS.put(normal.charAt(i), smallCaps.charAt(i));
        }

        // Czech diacritics mapping to base small caps
        SMALL_CAPS.put('á', 'ᴀ'); SMALL_CAPS.put('Á', 'ᴀ');
        SMALL_CAPS.put('č', 'ᴄ'); SMALL_CAPS.put('Č', 'ᴄ');
        SMALL_CAPS.put('ď', 'ᴅ'); SMALL_CAPS.put('Ď', 'ᴅ');
        SMALL_CAPS.put('é', 'ᴇ'); SMALL_CAPS.put('É', 'ᴇ');
        SMALL_CAPS.put('ě', 'ᴇ'); SMALL_CAPS.put('Ě', 'ᴇ');
        SMALL_CAPS.put('í', 'ɪ'); SMALL_CAPS.put('Í', 'ɪ');
        SMALL_CAPS.put('ň', 'ɴ'); SMALL_CAPS.put('Ň', 'ɴ');
        SMALL_CAPS.put('ó', 'ᴏ'); SMALL_CAPS.put('Ó', 'ᴏ');
        SMALL_CAPS.put('ř', 'ʀ'); SMALL_CAPS.put('Ř', 'ʀ');
        SMALL_CAPS.put('š', 's'); SMALL_CAPS.put('Š', 's');
        SMALL_CAPS.put('ť', 'ᴛ'); SMALL_CAPS.put('Ť', 'ᴛ');
        SMALL_CAPS.put('ú', 'ᴜ'); SMALL_CAPS.put('Ú', 'ᴜ');
        SMALL_CAPS.put('ů', 'ᴜ'); SMALL_CAPS.put('Ů', 'ᴜ');
        SMALL_CAPS.put('ý', 'ʏ'); SMALL_CAPS.put('Ý', 'ʏ');
        SMALL_CAPS.put('ž', 'ᴢ'); SMALL_CAPS.put('Ž', 'ᴢ');
    }

    public static String toSmallCaps(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            sb.append(SMALL_CAPS.getOrDefault(c, c));
        }
        return sb.toString();
    }

    public static Component parse(String input) {
        if (input == null) return Component.empty();

        Matcher matcher = CODE_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            String before = input.substring(lastEnd, matcher.start());
            sb.append(toSmallCaps(before));

            String code = matcher.group();
            if (code.startsWith("&#")) {
                String hex = code.substring(2);
                sb.append("§x");
                for (char c : hex.toCharArray()) {
                    sb.append("§").append(c);
                }
            } else {
                sb.append(code.replace("&", "§"));
            }
            lastEnd = matcher.end();
        }
        sb.append(toSmallCaps(input.substring(lastEnd)));

        return LegacyComponentSerializer.legacySection().deserialize(sb.toString())
                .decoration(TextDecoration.ITALIC, false);
    }
}
