package me.jules.apexbuild;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuildCommand implements CommandExecutor {

    private final ApexBuild plugin;

    public BuildCommand(ApexBuild plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Tento příkaz může použít pouze hráč.", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("build.use")) {
            player.sendMessage(Component.text("Nemáš oprávnění k použití tohoto příkazu.", NamedTextColor.RED));
            return true;
        }

        plugin.getBuildGUI().openGUI(player);

        return true;
    }
}
