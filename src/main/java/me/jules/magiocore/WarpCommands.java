package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WarpCommands implements CommandExecutor, TabCompleter {
    private final MagioCore plugin;
    private final WarpManager manager;

    public WarpCommands(MagioCore plugin, WarpManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "setwarp" -> {
                if (!player.hasPermission("magiocore.admin") && !player.isOp()) {
                    player.sendMessage(FontUtils.parse("§c" + "ɴᴀ ᴛᴏʜʟᴇ ɴᴇᴍáš ᴅᴏsᴛᴀᴛᴇčɴá ᴏᴘʀáᴠɴěɴí!"));
                    return true;
                }
                if (args.length == 0) {
                    player.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴜžɪᴛí: /sᴇᴛᴡᴀʀᴘ <ɴáᴢᴇᴠ>"));
                    return true;
                }
                String name = args[0];
                manager.setWarp(name, player.getLocation());
                player.sendMessage(FontUtils.parse("&#00ff44" + "ᴡᴀʀᴘ &#ffbb00" + name + " &#00ff44ʙʏʟ úsᴘěšɴě ɴᴀsᴛᴀᴠᴇɴ ɴᴀ ᴛᴠé ᴘᴏᴢɪᴄɪ."));
            }
            case "delwarp" -> {
                if (!player.hasPermission("magiocore.admin") && !player.isOp()) {
                    player.sendMessage(FontUtils.parse("§c" + "ɴᴀ ᴛᴏʜʟᴇ ɴᴇᴍáš ᴅᴏsᴛᴀᴛᴇčɴá ᴏᴘʀáᴠɴěɴí!"));
                    return true;
                }
                if (args.length == 0) {
                    player.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴜžɪᴛí: /ᴅᴇʟᴡᴀʀᴘ <ɴáᴢᴇᴠ>"));
                    return true;
                }
                String name = args[0];
                if (!manager.exists(name)) {
                    player.sendMessage(FontUtils.parse("§c" + "ᴛᴇɴᴛᴏ ᴡᴀʀᴘ ɴᴇᴇxɪsᴛᴜᴊᴇ."));
                    return true;
                }
                manager.deleteWarp(name);
                player.sendMessage(FontUtils.parse("§c" + "ᴡᴀʀᴘ &#ffbb00" + name + " §cʙʏʟ sᴍᴀᴢáɴ."));
            }
            case "warp" -> {
                if (args.length == 0) {
                    sendWarpList(player);
                    return true;
                }
                String name = args[0];
                Warp warp = manager.getWarp(name);
                if (warp == null) {
                    player.sendMessage(FontUtils.parse("§c" + "ᴡᴀʀᴘ s ɴáᴢᴠᴇᴍ &#ffbb00" + name + " §cɴᴇᴇxɪsᴛᴜᴊᴇ!"));
                    return true;
                }
                player.sendMessage(FontUtils.parse("§7" + "ᴛᴇʟᴇᴘᴏʀᴛᴜᴊɪ ɴᴀ ᴡᴀʀᴘ &#ffbb00" + warp.getName() + "§7..."));
                TeleportUtils.startTeleportCountdown(player, warp.getLocation(), plugin, success -> {});
            }
        }

        return true;
    }

    private void sendWarpList(Player player) {
        Collection<Warp> warps = manager.getWarps();
        if (warps.isEmpty()) {
            player.sendMessage(FontUtils.parse("&#ffbb00" + "ᴅᴏsᴛᴜᴘɴé ᴡᴀʀᴘʏ: §7" + "žáᴅɴé"));
            return;
        }

        List<Component> warpComponents = warps.stream()
                .map(Warp::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(name -> FontUtils.parse("&#00fbff" + name)
                        .hoverEvent(HoverEvent.showText(FontUtils.parse("§7" + "ᴋʟɪᴋɴɪ ᴘʀᴏ ᴛᴇʟᴇᴘᴏʀᴛ ɴᴀ ᴡᴀʀᴘ &#00fbff" + name)))
                        .clickEvent(ClickEvent.runCommand("/warp " + name)))
                .collect(Collectors.toList());

        Component list = Component.join(JoinConfiguration.separator(FontUtils.parse("§7, ")), warpComponents);
        player.sendMessage(FontUtils.parse("&#ffbb00" + "ᴅᴏsᴛᴜᴘɴé ᴡᴀʀᴘʏ: ").append(list));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return manager.getWarps().stream()
                    .map(Warp::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
