package me.jules.magiocore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {
    private final MagioCore plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> searchMode = new ConcurrentHashMap<>();

    public ChatListener(MagioCore plugin) {
        this.plugin = plugin;
    }

    public void setSearchMode(UUID uuid, boolean mode) {
        if (mode) searchMode.put(uuid, true);
        else searchMode.remove(uuid);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Check ignored players
        event.getRecipients().removeIf(viewerPlayer -> {
            return plugin.getIgnoreModule().isIgnored(viewerPlayer.getUniqueId(), player.getUniqueId());
        });

        String message = event.getMessage();

        if (ItemEditListener.pendingInput.containsKey(player.getUniqueId())) {
            return; // Handled by ItemEditListener
        }

        if (searchMode.getOrDefault(player.getUniqueId(), false)) {
            event.setCancelled(true);
            setSearchMode(player.getUniqueId(), false);
            plugin.getBaltopGui().handleSearch(player, message);
            return;
        }

        if (player.hasPermission("magiocore.admin") || player.isOp()) return;

        FileConfiguration config = plugin.getModuleManager().getModuleConfig("chat");
        long delay = config.getLong("anti-spam.delay", 2) * 1000L;
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player.getUniqueId())) {
            long last = cooldowns.get(player.getUniqueId());
            if (now - last < delay) {
                event.setCancelled(true);
                player.sendMessage(FontUtils.parse("§c" + "ɴᴇᴘíš ᴛᴀᴋ ʀʏᴄʜʟᴇ."));
                return;
            }
        }
        cooldowns.put(player.getUniqueId(), now);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
        searchMode.remove(event.getPlayer().getUniqueId());
    }
}
