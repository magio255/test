package me.jules.magiocore;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoinflipCommand implements CommandExecutor {
    private final MagioCore plugin;
    private final CoinflipManager manager;

    public CoinflipCommand(MagioCore plugin, CoinflipManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length > 0) {
            try {
                double amount = Double.parseDouble(args[0]);
                if (amount <= 0) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cSázka musí být kladná."));
                    return true;
                }

                if (plugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cNemáš dostatek peněz."));
                    return true;
                }

                plugin.getEconomy().withdrawPlayer(player, amount);
                manager.addBet(player, amount);
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bVytvořil jsi coinflip o §f" + amount + " §b$."));
            } catch (NumberFormatException e) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cPoužití: /coinflip [částka]"));
            }
            return true;
        }

        plugin.getCoinflipGui().open(player);
        return true;
    }
}
