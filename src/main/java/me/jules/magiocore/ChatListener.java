package me.jules.magiocore;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {
    private final MagioCore plugin;
    private final Map<UUID, Long> lastMessage = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> searchMode = new ConcurrentHashMap<>();

    public ChatListener(MagioCore plugin) {
        this.plugin = plugin;
    }

    public void setSearchMode(UUID uuid, boolean mode) {
        if (mode) searchMode.put(uuid, true);
        else searchMode.remove(uuid);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (searchMode.getOrDefault(player.getUniqueId(), false)) {
            event.setCancelled(true);
            setSearchMode(player.getUniqueId(), false);
            handleBaltopSearch(player, LegacyComponentSerializer.legacySection().serialize(event.originalMessage()));
            return;
        }

        if (player.isOp()) return;

        long now = System.currentTimeMillis();
        long delay = plugin.getConfig().getInt("anti-spam.delay", 2) * 1000L;

        if (lastMessage.containsKey(player.getUniqueId())) {
            long last = lastMessage.get(player.getUniqueId());
            if (now - last < delay) {
                event.setCancelled(true);
                player.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴍᴀʟʏ! ᴍᴜsíš ᴘᴏčᴋᴀᴛ ᴘřᴇᴅ ᴅᴀʟšíᴍ ᴘᴏsʟáɴíᴍ ᴢᴘʀáᴠʏ"));
                return;
            }
        }

        lastMessage.put(player.getUniqueId(), now);
    }

    private void handleBaltopSearch(Player player, String message) {
        List<BaltopManager.BaltopEntry> top = plugin.getBaltopManager().getCachedTop();
        int rank = 0;
        boolean found = false;

        for (int i = 0; i < top.size(); i++) {
            BaltopManager.BaltopEntry entry = top.get(i);
            if (entry.name().equalsIgnoreCase(message)) {
                found = true;
                rank = i + 1;
                player.sendMessage(FontUtils.parse("&#EA427Fʙᴀʟᴛᴏᴘ &#888888» §f" + "ʜʀáč " + "&#ffbb00" + entry.name() + " §fᴊᴇ ɴᴀ &#00fbff" + rank + ". §fᴍísᴛě s ʙᴀʟᴀɴᴄí &#00ff44" + entry.balance() + " $"));
                break;
            }
        }

        if (!found) {
            player.sendMessage(FontUtils.parse("&#EA427Fʙᴀʟᴛᴏᴘ &#888888» §f" + "ʜʀáč " + "&#ffbb00" + message + " §fɴᴇʙʏʟ ɴᴀʟᴇᴢᴇɴ ᴠ ʙᴀʟᴛᴏᴘᴜ"));
        }
    }
}
