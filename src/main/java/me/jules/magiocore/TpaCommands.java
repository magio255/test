package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TpaCommands implements CommandExecutor {
    private final MagioCore plugin;
    private final TpaManager tpaManager;

    private final String prefix = "§bᴛᴘᴀ §8» §7";
    private final String errorPrefix = "§cᴛᴘᴀ §8» §7";
    private final String color = "§b";

    public TpaCommands(MagioCore plugin, TpaManager tpaManager) {
        this.plugin = plugin;
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "tpa":
                handleTpa(player, args, "to");
                break;
            case "tpahere":
                handleTpa(player, args, "here");
                break;
            case "tpacancel":
                handleTpaCancel(player);
                break;
            case "tpaoff":
                handleTpaOff(player);
                break;
            case "tpaccept":
                handleTpaAccept(player);
                break;
            case "tpadeny":
                handleTpaDeny(player);
                break;
        }

        return true;
    }

    private void handleTpa(Player player, String[] args, String type) {
        if (args.length == 0) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ᴘᴏᴜžɪᴛí: /" + (type.equals("to") ? "ᴛᴘᴀ" : "ᴛᴘᴀʜᴇʀᴇ") + " <ʜʀáč>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ʜʀáč ɴᴇɴí ᴏɴʟɪɴᴇ."));
            return;
        }

        if (target.equals(player)) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ɴᴇᴍůžᴇš sᴇ ᴛᴇʟᴇᴘᴏʀᴛᴏᴠᴀᴛ sáᴍ ᴋ sᴇʙě!"));
            return;
        }

        if (tpaManager.isTpaOff(target.getUniqueId())) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ʜʀáč ᴍá ᴠʏᴘɴᴜᴛé žáᴅᴏsᴛɪ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ."));
            return;
        }

        tpaManager.sendRequest(player.getUniqueId(), target.getUniqueId(), type);

        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + (type.equals("to") ? "ᴢᴀsʟᴀʟ ᴊsɪ žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ʜʀáčɪ " : "ᴢᴀsʟᴀʟ ᴊsɪ žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ᴋ sᴇʙě ʜʀáčɪ ") + color + target.getName() + "§7."));

        target.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "ʜʀáč " + color + player.getName() + " §7" + (type.equals("to") ? "sᴇ ᴄʜᴄᴇ ᴛᴇʟᴇᴘᴏʀᴛᴏᴠᴀᴛ ᴋ ᴛᴏʙě." : "ᴄʜᴄᴇ, ᴀʙʏs sᴇ ᴛᴇʟᴇᴘᴏʀᴛᴏᴠᴀʟ ᴋ ɴěᴍᴜ.")));

        Component accept = LegacyComponentSerializer.legacySection().deserialize("§a§l[ᴘᴏᴛᴠʀᴅɪᴛ]")
                .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize("§aᴋʟɪᴋɴɪ ᴘʀᴏ ᴘᴏᴛᴠʀᴢᴇɴí")))
                .clickEvent(ClickEvent.runCommand("/tpaccept"));

        Component deny = LegacyComponentSerializer.legacySection().deserialize("§c§l[ᴏᴅᴍɪᴛɴᴏᴜᴛ]")
                .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize("§cᴋʟɪᴋɴɪ ᴘʀᴏ ᴏᴅᴍíᴛɴᴜᴛí")))
                .clickEvent(ClickEvent.runCommand("/tpadeny"));

        target.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix).append(accept).append(Component.text(" §7§l/ ")).append(deny));

        // Schedule expiry message
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            TpaManager.TpaRequest req = tpaManager.getRequest(target.getUniqueId());
            if (req != null && req.requester.equals(player.getUniqueId())) {
                tpaManager.removeRequest(target.getUniqueId());
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ᴘʀᴏ " + color + target.getName() + " §7ᴠʏᴘʀšᴇʟᴀ."));
            }
        }, 1200L); // 60 seconds
    }

    private void handleTpaCancel(Player player) {
        UUID targetUuid = tpaManager.getSentRequestTarget(player.getUniqueId());
        if (targetUuid != null) {
            Player target = Bukkit.getPlayer(targetUuid);
            tpaManager.removeRequest(targetUuid);
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "ᴢʀᴜšɪʟ ᴊsɪ žáᴅᴏsᴛ ᴘʀᴏ " + color + (target != null ? target.getName() : "ʜʀáčᴇ") + "§7."));
        } else {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ɴᴇᴍáš žáᴅɴᴏᴜ ᴏᴅᴇsʟᴀɴᴏᴜ žáᴅᴏsᴛ."));
        }
    }

    private void handleTpaOff(Player player) {
        tpaManager.toggleTpa(player.getUniqueId());
        if (tpaManager.isTpaOff(player.getUniqueId())) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "žáᴅᴏsᴛɪ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ʙʏʟʏ §ᴄᴠʏᴘɴᴜᴛʏ."));
        } else {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "žáᴅᴏsᴛɪ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ʙʏʟʏ §ᴀᴢᴀᴘɴᴜᴛʏ."));
        }
    }

    private void handleTpaAccept(Player player) {
        TpaManager.TpaRequest req = tpaManager.getRequest(player.getUniqueId());
        if (req == null) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ɴᴇᴍáš žáᴅɴᴏᴜ ᴀᴋᴛɪᴠɴí žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ."));
            return;
        }

        Player requester = Bukkit.getPlayer(req.requester);
        if (requester == null) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ʜʀáč ᴊɪž ɴᴇɴí ᴘřɪᴘᴏᴊᴇɴ."));
            tpaManager.removeRequest(player.getUniqueId());
            return;
        }

        tpaManager.removeRequest(player.getUniqueId());

        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "ᴘʀɪᴊᴀʟ ᴊsɪ žáᴅᴏsᴛ. ᴛᴇʟᴇᴘᴏʀᴛᴀᴄᴇ ᴢᴀ 3s..."));
        requester.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "ʜʀáč " + color + player.getName() + " §7ᴘʀɪᴊᴀʟ ᴛᴠᴏᴊí žáᴅᴏsᴛ. ᴛᴇʟᴇᴘᴏʀᴛᴀᴄᴇ ᴢᴀ 3s..."));

        Player toTeleport = req.type.equals("to") ? requester : player;
        Player targetLocPlayer = req.type.equals("to") ? player : requester;

        TeleportUtils.startTeleportCountdown(toTeleport, targetLocPlayer, plugin, success -> {
            if (success) {
                // messages are already handled by TeleportUtils, but we can add more if needed
            }
        });
    }

    private void handleTpaDeny(Player player) {
        TpaManager.TpaRequest req = tpaManager.getRequest(player.getUniqueId());
        if (req == null) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(errorPrefix + "ɴᴇᴍáš žáᴅɴᴏᴜ ᴀᴋᴛɪᴠɴí žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ."));
            return;
        }

        Player requester = Bukkit.getPlayer(req.requester);
        tpaManager.removeRequest(player.getUniqueId());

        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "ᴏᴅᴍíᴛʟ ᴊsɪ žáᴅᴏsᴛ."));
        if (requester != null) {
            requester.sendMessage(LegacyComponentSerializer.legacySection().deserialize(prefix + "ʜʀáč " + color + player.getName() + " §7ᴏᴅᴍíᴛʟ ᴛᴠᴏᴊí žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ."));
        }
    }
}
