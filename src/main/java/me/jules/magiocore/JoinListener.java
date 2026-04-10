package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class JoinListener implements Listener {
    private final MagioCore plugin;

    public JoinListener(MagioCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean showHead = plugin.getConfig().getBoolean("join-message.show-head", true);
        String customJoinMessage = plugin.getConfig().getString("join-message.format", "§7Vítej na serveru, §b%player%§7!");
        List<String> sideMessages = plugin.getConfig().getStringList("join-message.side-messages");

        // Disable default join message
        event.joinMessage(null);

        if (showHead) {
            SkinUtils.getHeadRows(player).thenAccept(rows -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (int i = 0; i < 8; i++) {
                        Component row = rows.get(i);
                        String sideText = "";
                        if (i < sideMessages.size()) {
                            sideText = " " + sideMessages.get(i).replace("%player%", player.getName());
                        } else if (i == 4) { // Default middle if no side messages
                            sideText = " " + customJoinMessage.replace("%player%", player.getName());
                        }

                        Bukkit.broadcast(row.append(LegacyComponentSerializer.legacySection().deserialize(sideText)));
                    }
                });
            });
        } else {
            Bukkit.broadcast(LegacyComponentSerializer.legacySection().deserialize(customJoinMessage.replace("%player%", player.getName())));
        }
    }
}
