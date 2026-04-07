package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class TeleportUtils {
    private static final Map<UUID, BukkitRunnable> pendingTeleports = new HashMap<>();

    public static void startTeleportCountdown(Player player, Location target, int seconds, MagioCore plugin, Consumer<Boolean> callback) {
        startTeleportCountdown(player, target, null, seconds, plugin, callback);
    }

    public static void startTeleportCountdown(Player player, Player targetPlayer, int seconds, MagioCore plugin, Consumer<Boolean> callback) {
        startTeleportCountdown(player, null, targetPlayer, seconds, plugin, callback);
    }

    private static void startTeleportCountdown(Player player, Location targetLoc, Player targetPlayer, int seconds, MagioCore plugin, Consumer<Boolean> callback) {
        cancelPendingTeleport(player);

        Location startLocation = player.getLocation().clone();

        BukkitRunnable task = new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    pendingTeleports.remove(player.getUniqueId());
                    callback.accept(false);
                    return;
                }

                if (player.getLocation().distanceSquared(startLocation) > 0.25) { // 0.5 distance limit
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cᴛᴇʟᴇᴘᴏʀᴛᴀᴄᴇ ᴢʀᴜšᴇɴᴀ! ᴘᴏʜɴᴜʟ ᴊsɪ sᴇ."));
                    cancel();
                    pendingTeleports.remove(player.getUniqueId());
                    callback.accept(false);
                    return;
                }

                if (targetPlayer != null && !targetPlayer.isOnline()) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cᴛᴇʟᴇᴘᴏʀᴛᴀᴄᴇ ᴢʀᴜšᴇɴᴀ! ʜʀáč sᴇ ᴏᴅᴘᴏᴊɪʟ."));
                    cancel();
                    pendingTeleports.remove(player.getUniqueId());
                    callback.accept(false);
                    return;
                }

                if (remaining <= 0) {
                    if (targetPlayer != null) {
                        player.teleport(targetPlayer.getLocation());
                    } else {
                        player.teleport(targetLoc);
                    }
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bʙʏʟ ᴊsɪ ᴛᴇʟᴇᴘᴏʀᴛᴏᴠáɴ."));
                    cancel();
                    pendingTeleports.remove(player.getUniqueId());
                    callback.accept(true);
                    return;
                }

                player.sendActionBar(LegacyComponentSerializer.legacySection().deserialize("§bᴛᴇʟᴇᴘᴏʀᴛᴀᴄᴇ ᴢᴀ " + remaining + "s..."));
                remaining--;
            }
        };

        task.runTaskTimer(plugin, 0L, 20L);
        pendingTeleports.put(player.getUniqueId(), task);
    }

    public static void cancelPendingTeleport(Player player) {
        BukkitRunnable existing = pendingTeleports.remove(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }
    }
}
