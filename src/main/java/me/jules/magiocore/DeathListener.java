package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            // Killed by player
            // » ☠ victim, was killed by ⚔ killer
            Component message = FontUtils.parse("&#888888» &#ff0000☠ &#ff0000" + victim.getName() + "§7, was killed by &#ff0000⚔ &#00fbff" + killer.getName());
            event.deathMessage(message);
        } else {
            // Normal death
            // » ☠ victim has died
            Component message = FontUtils.parse("&#888888» &#ff0000☠ &#ff0000" + victim.getName() + " §7has died");
            event.deathMessage(message);
        }

        // Module logic for titles
        MagioCore plugin = MagioCore.getPlugin(MagioCore.class);
        if (plugin.getModuleManager().isEnabled("deathsystem")) {
            String dagger = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(FontUtils.parse("&#ff0000" + "🗡"));
            String victimSub = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(FontUtils.parse("§fʏᴏᴜ ʜᴀᴠᴇ ʙᴇᴇɴ ᴋɪʟʟᴇᴅ ʙʏ " + (killer != null ? killer.getName() : "ᴇɴᴠɪʀᴏɴᴍᴇɴᴛ")));
            victim.sendTitle(dagger, victimSub, 10, 40, 10);
            if (killer != null) {
                String killerSub = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(FontUtils.parse("§fʏᴏᴜ ʜᴀᴠᴇ ᴋɪʟʟᴇᴅ " + victim.getName()));
                killer.sendTitle(dagger, killerSub, 10, 40, 10);
            }
        }
    }
}
