package me.jules.magiocore.modules;

import me.jules.magiocore.MagioCore;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AntiGriefModule implements Listener {
    private final MagioCore plugin;

    public AntiGriefModule(MagioCore plugin) {
        this.plugin = plugin;
        startDifficultyTask();
    }

    private void startDifficultyTask() {
        if (!plugin.getConfig().getBoolean("antigrief.difficulty-task", true)) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getWorlds().forEach(world -> world.setDifficulty(Difficulty.HARD));
            }
        }.runTaskTimer(plugin, 0L, 12000L); // Every 10 mins
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();
        List<String> bannedWords = plugin.getConfig().getStringList("antigrief.banned-words");

        for (String word : bannedWords) {
            if (message.contains(word.toLowerCase())) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + event.getPlayer().getName() + " 30d Banned words/Seed");
                });
                break;
            }
        }
    }
}
