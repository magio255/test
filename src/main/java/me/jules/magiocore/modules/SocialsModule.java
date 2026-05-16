package me.jules.magiocore.modules;

import me.jules.magiocore.FontUtils;
import me.jules.magiocore.MagioCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class SocialsModule implements CommandExecutor {
    private final MagioCore plugin;

    public SocialsModule(MagioCore plugin) {
        this.plugin = plugin;
        startBroadcastTask();
    }

    private void startBroadcastTask() {
        FileConfiguration config = plugin.getModuleManager().getModuleConfig("socials");
        int interval = config.getInt("broadcast.interval", 15) * 1200;
        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration c = plugin.getModuleManager().getModuleConfig("socials");
                for (String msg : c.getStringList("broadcast.messages")) {
                    if (msg.isEmpty()) {
                        Bukkit.broadcast(Component.empty());
                    } else {
                        Bukkit.broadcast(FontUtils.parse(msg));
                    }
                }

                String soundName = c.getString("broadcast.sound", "BLOCK_NOTE_BLOCK_PLING");
                float vol = (float) c.getDouble("broadcast.volume", 1.0);
                float pitch = (float) c.getDouble("broadcast.pitch", 2.0);

                try {
                    Sound sound = Sound.valueOf(soundName);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), sound, vol, pitch);
                    }
                } catch (Exception ignored) {}
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfiguration config = plugin.getModuleManager().getModuleConfig("socials");
        if (command.getName().equalsIgnoreCase("discord")) {
            String prefix = config.getString("discord.prefix");
            String message = config.getString("discord.message");
            String link = config.getString("discord.link");

            sender.sendMessage(FontUtils.parse(prefix));
            sender.sendMessage("");
            sender.sendMessage(FontUtils.parse(message));

            Component linkComp = FontUtils.parse("&#888888⤷ &n" + link, false)
                    .clickEvent(ClickEvent.openUrl(link));
            sender.sendMessage(linkComp);
            return true;
        }

        if (command.getName().equalsIgnoreCase("store")) {
            String prefix = config.getString("store.prefix");
            String message = config.getString("store.message");
            String link = config.getString("store.link");

            sender.sendMessage(FontUtils.parse(prefix));
            sender.sendMessage("");
            sender.sendMessage(FontUtils.parse(message));

            Component linkComp = FontUtils.parse("&#888888⤷ &n" + link, false)
                    .clickEvent(ClickEvent.openUrl(link));
            sender.sendMessage(linkComp);
            return true;
        }

        return false;
    }
}
