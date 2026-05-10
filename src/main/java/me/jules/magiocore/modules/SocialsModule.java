package me.jules.magiocore.modules;

import me.jules.magiocore.FontUtils;
import me.jules.magiocore.MagioCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
        int interval = plugin.getConfig().getInt("socials.broadcast.interval", 15) * 1200;
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcast(Component.empty());
                Bukkit.broadcast(FontUtils.parse("&#5865F2&l                    ᴘŘɪᴘᴏᴊ sᴇ ɴᴀ ɴáš ᴅɪsᴄᴏʀᴅ"));
                Bukkit.broadcast(Component.empty());
                Bukkit.broadcast(FontUtils.parse("§7       ʙᴜď ᴠ ᴏʙʀᴀᴢᴇ, ᴄʜᴀᴛᴜᴊ s ᴏsᴛᴀᴛɴíᴍɪ ᴀ ᴢísᴋᴇᴊ ᴘᴏᴅᴘᴏʀᴜ!"));
                Bukkit.broadcast(FontUtils.parse("§7              ᴘŘɪᴘᴏᴊ sᴇ ᴋ ɴáᴍ ɴᴀ &#5865F2&nᴅɪsᴄᴏʀᴅ ᴊᴇšᴛě ᴅɴᴇs!"));
                Bukkit.broadcast(Component.empty());
                Bukkit.broadcast(FontUtils.parse("§f                           → &#5865F2&l/ᴅɪsᴄᴏʀᴅ &f←"));
                Bukkit.broadcast(Component.empty());

                String soundName = plugin.getConfig().getString("socials.broadcast.sound", "BLOCK_NOTE_BLOCK_PLING");
                float vol = (float) plugin.getConfig().getDouble("socials.broadcast.volume", 1.0);
                float pitch = (float) plugin.getConfig().getDouble("socials.broadcast.pitch", 2.0);

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
        if (command.getName().equalsIgnoreCase("discord")) {
            String prefix = plugin.getConfig().getString("socials.discord.prefix");
            String message = plugin.getConfig().getString("socials.discord.message");
            String link = plugin.getConfig().getString("socials.discord.link");

            sender.sendMessage(FontUtils.parse(prefix));
            sender.sendMessage("");
            sender.sendMessage(FontUtils.parse(message));

            Component linkComp = FontUtils.parse("&#888888⤷ &n" + link, false)
                    .clickEvent(ClickEvent.openUrl(link));
            sender.sendMessage(linkComp);
            return true;
        }

        if (command.getName().equalsIgnoreCase("store")) {
            String prefix = plugin.getConfig().getString("socials.store.prefix");
            String message = plugin.getConfig().getString("socials.store.message");
            String link = plugin.getConfig().getString("socials.store.link");

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
