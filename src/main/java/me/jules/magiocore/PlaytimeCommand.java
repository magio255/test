package me.jules.magiocore;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaytimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cTento hráč není online."));
                return true;
            }
            sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bHrac §f" + target.getName() + " §bnahral: §f" + PlaytimeUtils.formatPlaytime(target)));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tento prikaz je jen pro hrace.");
            return true;
        }

        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bTvuj odehrany cas: §f" + PlaytimeUtils.formatPlaytime(player)));
        return true;
    }
}
