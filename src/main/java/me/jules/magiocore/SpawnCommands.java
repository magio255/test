package me.jules.magiocore;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommands implements CommandExecutor {
    private final MagioCore plugin;

    public SpawnCommands(MagioCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!player.hasPermission("magiocore.admin")) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cNemáš oprávnění."));
                return true;
            }
            plugin.getConfig().set("spawn", player.getLocation());
            plugin.saveConfig();
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bSpawn byl nastaven."));
            return true;
        }

        if (command.getName().equalsIgnoreCase("spawn")) {
            Location spawn = plugin.getConfig().getLocation("spawn");
            if (spawn == null) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cSpawn není nastaven."));
                return true;
            }

            TeleportUtils.startTeleportCountdown(player, spawn, plugin, success -> {});
            return true;
        }

        return false;
    }
}
