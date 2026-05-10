package me.jules.magiocore.modules;

import me.jules.magiocore.FontUtils;
import me.jules.magiocore.MagioCore;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AutoRestartModule {
    private final MagioCore plugin;
    private boolean restarting = false;

    public AutoRestartModule(MagioCore plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (restarting) return;

                String restartTimeStr = plugin.getConfig().getString("autorestart.time", "00:00");
                String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

                if (now.equals(restartTimeStr)) {
                    startCountdown();
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L); // Check every minute
    }

    private void startCountdown() {
        restarting = true;

        new BukkitRunnable() {
            int secondsLeft = 300; // 5 minutes

            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    cancel();
                    Bukkit.broadcast(FontUtils.parse("&#ff0000" + "ʀᴇsᴛᴀʀᴛᴜᴊɪ sᴇʀᴠᴇʀ..."));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    return;
                }

                if (shouldAnnounce(secondsLeft)) {
                    String timeMsg = formatTime(secondsLeft);
                    String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(FontUtils.parse("&#ff0000" + "sᴇʀᴠᴇʀ ɪs ʀᴇsᴛᴀʀᴛɪɴɢ ɪɴ " + timeMsg + "!"));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(title, "", 10, 40, 10);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
                    }
                }

                secondsLeft--;
                if (secondsLeft < 5 && secondsLeft > 0) {
                     // handled by shouldAnnounce
                }
            }

            private boolean shouldAnnounce(int sec) {
                return sec == 300 || sec == 240 || sec == 180 || sec == 120 || sec == 60 || sec == 30 || sec == 15 || sec == 10 || sec <= 5;
            }

            private String formatTime(int sec) {
                if (sec >= 60) {
                    int min = sec / 60;
                    return min + (min == 1 ? " ᴍɪɴᴜᴛᴇ" : " ᴍɪɴᴜᴛᴇs");
                }
                return sec + (sec == 1 ? " sᴇᴄᴏɴᴅ" : " sᴇᴄᴏɴᴅs");
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
