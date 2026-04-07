package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeCommands implements CommandExecutor {
    private final MagioCore plugin;
    private final HomeManager homeManager;

    public HomeCommands(MagioCore plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is only for players.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("sethome")) {
            int number = 1;
            if (args.length > 0) {
                try {
                    number = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cPoužití: /sethome [1-7]"));
                    return true;
                }
            }

            if (number < 1 || number > 7) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cČíslo domova musí být mezi 1 a 7."));
                return true;
            }

            homeManager.setHome(player.getUniqueId(), number, player.getLocation());
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bNastavil jsi si domov č. " + number + "."));
            return true;
        }

        if (command.getName().equalsIgnoreCase("home")) {
            if (args.length > 0) {
                int number;
                try {
                    number = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cPoužití: /home [1-7]"));
                    return true;
                }

                Home home = homeManager.getHome(player.getUniqueId(), number);
                if (home == null) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cTento domov nemáš nastavený."));
                    return true;
                }

                TeleportUtils.startTeleportCountdown(player, home.getLocation(), 3, plugin, success -> {});
                return true;
            } else {
                // Open GUI (this will be implemented in the next step, for now just a placeholder or keep as is)
                // Actually I can call a GUI opening method here if I implement it in MagioCore or a separate class.
                // I will add a GUI open call once I have the GUI class.
                plugin.getHomeGui().open(player);
                return true;
            }
        }

        return false;
    }
}
