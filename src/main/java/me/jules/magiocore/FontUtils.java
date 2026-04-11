package me.jules.magiocore;

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
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZáčďéěíňóřšťúůýžÁČĎÉĚÍŇÓŘŠŤÚŮÝŽ";
        String smallCaps = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";
        // Diacritics mapping to base Small Caps
        String czech = "áčďéěíňóřšťúůýž";
        String base = "acdeeinorstuuuz";

        for (int i = 0; i < 52; i++) { // a-z A-Z
            SMALL_CAPS.put(normal.charAt(i), smallCaps.charAt(i));
        }

        for (int i = 0; i < czech.length(); i++) {
            char lower = czech.charAt(i);
            char upper = Character.toUpperCase(lower);
            char target = toSmallCaps(String.valueOf(base.charAt(i))).charAt(0);
            SMALL_CAPS.put(lower, target);
            SMALL_CAPS.put(upper, target);
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
