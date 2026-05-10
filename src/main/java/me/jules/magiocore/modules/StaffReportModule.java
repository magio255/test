package me.jules.magiocore.modules;

import me.jules.magiocore.FontUtils;
import me.jules.magiocore.MagioCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StaffReportModule implements CommandExecutor {
    private final MagioCore plugin;
    private final List<String> reports = new ArrayList<>();

    public StaffReportModule(MagioCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("staffchat")) {
            if (!sender.hasPermission("staff.chat")) {
                sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴏᴘʀáᴠɴěɴí."));
                return true;
            }
            if (args.length == 0) return false;

            String message = String.join(" ", args);
            String format = "§c§lSTAFFCHAT §e" + sender.getName() + "§7: §f" + message;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("staff.chat")) {
                    player.sendMessage(format);
                }
            }
            Bukkit.getConsoleSender().sendMessage(format);
            return true;
        }

        if (command.getName().equalsIgnoreCase("report")) {
            if (!sender.hasPermission("report.use")) {
                sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴏᴘʀáᴠɴěɴí ᴘᴏᴜžíᴛ /ʀᴇᴘᴏʀᴛ."));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴜžɪᴛí: /ʀᴇᴘᴏʀᴛ <ʜʀáč> <ᴅůᴠᴏᴅ>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(FontUtils.parse("§c" + "ᴛᴇɴᴛᴏ ʜʀáč ɴᴇɴí ᴏɴʟɪɴᴇ."));
                return true;
            }

            String reason = "";
            for(int i = 1; i < args.length; i++) reason += args[i] + " ";
            reason = reason.trim();

            reports.add(sender.getName() + " nahlásil " + target.getName() + ": " + reason);
            sender.sendMessage(FontUtils.parse("&#00ff44" + "ɴᴀʜʟásɪʟ ᴊsɪ ʜʀáčᴇ &#f1c40f" + target.getName() + " &#00ff44s ᴅůᴠᴏᴅᴇᴍ: §f" + reason));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("report.check")) {
                    player.sendMessage(FontUtils.parse("§c§lREPORT §e" + sender.getName() + " §7nahlásil §e" + target.getName() + "§7: §f" + reason));
                }
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("checkreport")) {
            if (!sender.hasPermission("report.check")) {
                sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴏᴘʀáᴠɴěɴí ᴠɪᴅěᴛ ʀᴇᴘᴏʀᴛʏ."));
                return true;
            }
            if (reports.isEmpty()) {
                sender.sendMessage(FontUtils.parse("§7" + "žáᴅɴé ʀᴇᴘᴏʀᴛʏ ɴᴇᴊsᴏᴜ ᴋ ᴅɪsᴘᴏᴢɪᴄɪ."));
                return true;
            }
            sender.sendMessage(FontUtils.parse("&#f1c40f" + "===== ʀᴇᴘᴏʀᴛʏ ====="));
            for (String report : reports) {
                sender.sendMessage(FontUtils.parse("&#f1c40f" + report));
            }
            sender.sendMessage(FontUtils.parse("&#f1c40f" + "==================="));
            return true;
        }

        if (command.getName().equalsIgnoreCase("clearreports")) {
            if (!sender.hasPermission("report.admin")) {
                sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴏᴘʀáᴠɴěɴí."));
                return true;
            }
            reports.clear();
            sender.sendMessage(FontUtils.parse("&#00ff44" + "ᴠšᴇᴄʜɴʏ ʀᴇᴘᴏʀᴛʏ ʙʏʟʏ sᴍᴀᴢáɴʏ."));
            return true;
        }

        return false;
    }
}
