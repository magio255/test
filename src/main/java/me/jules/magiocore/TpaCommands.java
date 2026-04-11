package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TpaCommands implements CommandExecutor {
    private final MagioCore plugin;
    private final TpaManager tpaManager;

    private final String prefix = "&#00fbff&lᴛᴘᴀ &#888888» §7";
    private final String errorPrefix = "§cᴛᴘᴀ &#888888» §7";
    private final String color = "&#00fbff&l";

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
            player.sendMessage(FontUtils.parse(errorPrefix + "ᴘᴏᴜžɪᴛí: /" + (type.equals("to") ? "ᴛᴘᴀ" : "ᴛᴘᴀʜᴇʀᴇ") + " <ʜʀáč> ✖"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(FontUtils.parse(errorPrefix + "ᴛᴇɴᴛᴏ ʜʀáč ɴᴇɴí ᴏɴʟɪɴᴇ ✖"));
            return;
        }

        if (target.equals(player)) {
            player.sendMessage(FontUtils.parse(errorPrefix + "ɴᴇᴍůžᴇš sᴇ ᴛᴇʟᴇᴘᴏʀᴛᴏᴠᴀᴛ sáᴍ ᴋ sᴇʙě ✖"));
            return;
        }

        if (tpaManager.isTpaOff(target.getUniqueId())) {
            player.sendMessage(FontUtils.parse(errorPrefix + "ʜʀáč ᴍá ᴠʏᴘɴᴜᴛé žáᴅᴏsᴛɪ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ✖"));
            return;
        }

        tpaManager.sendRequest(player.getUniqueId(), target.getUniqueId(), type);

        player.sendMessage(FontUtils.parse(prefix + (type.equals("to") ? "ᴢᴀsʟᴀʟ ᴊsɪ žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ʜʀáčɪ " : "ᴢᴀsʟᴀʟ ᴊsɪ žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ᴋ sᴏʙě ʜʀáčɪ ") + color + target.getName() + " §7✔"));

        target.sendMessage(FontUtils.parse(prefix + "ʜʀáč " + color + player.getName() + " §7" + (type.equals("to") ? "sᴇ ᴄʜᴄᴇ ᴛᴇʟᴇᴘᴏʀᴛᴏᴠᴀᴛ ᴋ ᴛᴏʙě." : "ᴄʜᴄᴇ, ᴀʙʏs sᴇ ᴛᴇʟᴇᴘᴏʀᴛᴏᴠᴀʟ ᴋ ɴěᴍᴜ.")));

        Component accept = FontUtils.parse("&#00ff44&l[ᴘᴏᴛᴠʀᴅɪᴛ]")
                .hoverEvent(HoverEvent.showText(FontUtils.parse("&#00ff44&lᴋʟɪᴋɴɪ ᴘʀᴏ ᴘᴏᴛᴠʀᴢᴇɴí")))
                .clickEvent(ClickEvent.runCommand("/tpaccept"));

        Component deny = FontUtils.parse("§c§l[ᴏᴅᴍíᴛɴᴏᴜᴛ]")
                .hoverEvent(HoverEvent.showText(FontUtils.parse("§cᴋʟɪᴋɴɪ ᴘʀᴏ ᴏᴅᴍíᴛɴᴜᴛí")))
                .clickEvent(ClickEvent.runCommand("/tpadeny"));

        target.sendMessage(FontUtils.parse(prefix).append(accept).append(Component.text(" §7§l/ ")).append(deny));

        // Schedule expiry message
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            TpaManager.TpaRequest req = tpaManager.getRequest(target.getUniqueId());
            if (req != null && req.requester.equals(player.getUniqueId())) {
                tpaManager.removeRequest(target.getUniqueId());
                player.sendMessage(FontUtils.parse(prefix + "žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ᴘʀᴏ " + color + target.getName() + " §7ᴠʏᴘʀšᴇʟᴀ ✖"));
            }
        }, 1200L); // 60 seconds
    }

    private void handleTpaCancel(Player player) {
        UUID targetUuid = tpaManager.getSentRequestTarget(player.getUniqueId());
        if (targetUuid != null) {
            Player target = Bukkit.getPlayer(targetUuid);
            tpaManager.removeRequest(targetUuid);
            player.sendMessage(FontUtils.parse(prefix + "ᴢʀᴜšɪʟ ᴊsɪ žáᴅᴏsᴛ ᴘʀᴏ " + color + (target != null ? target.getName() : "ʜʀáčᴇ") + " §7✖"));
        } else {
            player.sendMessage(FontUtils.parse(errorPrefix + "ɴᴇᴍáš žáᴅɴᴏᴜ ᴏᴅᴇsʟᴀɴᴏᴜ žáᴅᴏsᴛ ✖"));
        }
    }

    private void handleTpaOff(Player player) {
        tpaManager.toggleTpa(player.getUniqueId());
        if (tpaManager.isTpaOff(player.getUniqueId())) {
            player.sendMessage(FontUtils.parse(prefix + "žáᴅᴏsᴛɪ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ʙʏʟʏ &#EA427F&lᴠʏᴘɴᴜᴛʏ ✖"));
        } else {
            player.sendMessage(FontUtils.parse(prefix + "žáᴅᴏsᴛɪ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ʙʏʟʏ &#00ff44&lᴢᴀᴘɴᴜᴛʏ ✔"));
        }
    }

    private void handleTpaAccept(Player player) {
        TpaManager.TpaRequest req = tpaManager.getRequest(player.getUniqueId());
        if (req == null) {
            player.sendMessage(FontUtils.parse(errorPrefix + "ɴᴇᴍáš žáᴅɴᴏᴜ ᴀᴋᴛɪᴠɴí žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ✖"));
            return;
        }

        Player requester = Bukkit.getPlayer(req.requester);
        if (requester == null) {
            player.sendMessage(FontUtils.parse(errorPrefix + "ʜʀáč ᴊɪž ɴᴇɴí ᴘřɪᴘᴏᴊᴇɴ ✖"));
            tpaManager.removeRequest(player.getUniqueId());
            return;
        }

        tpaManager.removeRequest(player.getUniqueId());

        player.sendMessage(FontUtils.parse(prefix + "ᴘřɪᴊᴀʟ ᴊsɪ žáᴅᴏsᴛ. ᴛᴇʟᴇᴘᴏʀᴛᴀᴄᴇ ᴢᴀ 3s... ✈"));
        requester.sendMessage(FontUtils.parse(prefix + "ʜʀáč " + color + player.getName() + " §7ᴘřɪᴊᴀʟ ᴛᴠᴏᴊí žáᴅᴏsᴛ. ᴛᴇʟᴇᴘᴏʀᴛᴀᴄᴇ ᴢᴀ 3s... ✈"));

        Player toTeleport = req.type.equals("to") ? requester : player;
        Player targetLocPlayer = req.type.equals("to") ? player : requester;

        TeleportUtils.startTeleportCountdown(toTeleport, targetLocPlayer, plugin, success -> {});
    }

    private void handleTpaDeny(Player player) {
        TpaManager.TpaRequest req = tpaManager.getRequest(player.getUniqueId());
        if (req == null) {
            player.sendMessage(FontUtils.parse(errorPrefix + "ɴᴇᴍáš žáᴅɴᴏᴜ ᴀᴋᴛɪᴠɴí žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ✖"));
            return;
        }

        Player requester = Bukkit.getPlayer(req.requester);
        tpaManager.removeRequest(player.getUniqueId());

        player.sendMessage(FontUtils.parse(prefix + "ᴏᴅᴍíᴛʟ ᴊsɪ žáᴅᴏsᴛ ✖"));
        if (requester != null) {
            requester.sendMessage(FontUtils.parse(prefix + "ʜʀáč " + color + player.getName() + " §7ᴏᴅᴍíᴛʟ ᴛᴠᴏᴊí žáᴅᴏsᴛ ᴏ ᴛᴇʟᴇᴘᴏʀᴛ ✖"));
        }
    }
}
