package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCommand implements CommandExecutor {
    private static final Map<UUID, UUID> lastMessaged = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("msg")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Pouze pro hráče.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴜžɪᴛí: /ᴍsɢ <ʜʀáč> <ᴢᴘʀáᴠᴀ>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(FontUtils.parse("§c" + "ʜʀáč ɴᴇɴí ᴏɴʟɪɴᴇ."));
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }

            sendPrivateMessage(player, target, message.toString().trim());
            return true;
        }

        if (command.getName().equalsIgnoreCase("reply")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Pouze pro hráče.");
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴜžɪᴛí: /ʀ <ᴢᴘʀáᴠᴀ>"));
                return true;
            }

            UUID targetUUID = lastMessaged.get(player.getUniqueId());
            if (targetUUID == null) {
                player.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴋᴏᴍᴜ ᴏᴅᴘᴏᴠěᴅěᴛ."));
                return true;
            }

            Player target = Bukkit.getPlayer(targetUUID);
            if (target == null) {
                player.sendMessage(FontUtils.parse("§c" + "ʜʀáč ᴊíž ɴᴇɴí ᴏɴʟɪɴᴇ."));
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }

            sendPrivateMessage(player, target, message.toString().trim());
            return true;
        }

        return false;
    }

    private void sendPrivateMessage(Player sender, Player target, String message) {
        // Format: Name » Name: Message
        // Colors: Sender(Blue), Arrow(Gray), Target(Blue), Colon(Gray), Message(White)
        String senderName = sender.getName();
        String targetName = target.getName();

        Component toSender = FontUtils.parse("&#00fbff" + senderName + " &#888888» &#00fbff" + targetName + "§7: §f" + message);
        Component toTarget = FontUtils.parse("&#00fbff" + senderName + " &#888888» &#00fbff" + targetName + "§7: §f" + message);

        sender.sendMessage(toSender);
        target.sendMessage(toTarget);

        lastMessaged.put(sender.getUniqueId(), target.getUniqueId());
        lastMessaged.put(target.getUniqueId(), sender.getUniqueId());
    }
}
