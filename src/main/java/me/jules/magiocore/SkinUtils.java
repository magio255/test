package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkinUtils {

    public static CompletableFuture<List<Component>> getHeadRows(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL skinUrl = player.getPlayerProfile().getTextures().getSkin();
                if (skinUrl == null) return getDefaultHead();

                BufferedImage image = ImageIO.read(skinUrl);
                List<Component> rows = new ArrayList<>();

                // The head is at 8,8 to 16,16 in the skin file
                // The outer layer (hat) is at 40,8 to 48,16
                for (int y = 8; y < 16; y++) {
                    Component row = Component.empty();
                    for (int x = 8; x < 16; x++) {
                        int rgb = image.getRGB(x, y);

                        // Check overlay (hat layer)
                        // In 64x64 skins, hat is at x+32
                        int overlayX = x + 32;
                        int overlayY = y;
                        int overlayRgb = image.getRGB(overlayX, overlayY);

                        // If alpha of overlay is not 0, use it
                        if (((overlayRgb >> 24) & 0xFF) > 0) {
                            rgb = overlayRgb;
                        }

                        row = row.append(Component.text("█").color(TextColor.color(rgb)));
                    }
                    rows.add(row);
                }
                return rows;
            } catch (Exception e) {
                return getDefaultHead();
            }
        });
    }

    private static List<Component> getDefaultHead() {
        List<Component> rows = new ArrayList<>();
        // Steve head colors approximation if fetch fails
        for (int i = 0; i < 8; i++) {
            rows.add(LegacyComponentSerializer.legacySection().deserialize("§8████████"));
        }
        return rows;
    }
}
