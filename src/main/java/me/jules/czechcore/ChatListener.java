package me.jules.czechcore;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {
    private final CzechCore plugin;
    private final Map<UUID, Long> lastMessage = new HashMap<>();

    public ChatListener(CzechCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) return;

        long now = System.currentTimeMillis();
        long delay = plugin.getConfig().getInt("anti-spam.delay", 2) * 1000L;

        if (lastMessage.containsKey(player.getUniqueId())) {
            long last = lastMessage.get(player.getUniqueId());
            if (now - last < delay) {
                event.setCancelled(true);
                player.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴍᴀʟʏ! ᴍᴜsíš ᴘᴏčᴋᴀᴛ ᴘřᴇᴅ ᴅᴀʟšíᴍ ᴘᴏsʟáɴíᴍ ᴢᴘʀáᴠʏ ✖"));
                return;
            }
        }

        lastMessage.put(player.getUniqueId(), now);
    }
}
