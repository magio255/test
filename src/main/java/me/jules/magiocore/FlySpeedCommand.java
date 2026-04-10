package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlySpeedCommand implements CommandExecutor, Listener {
    private final String title = "§bFly Speed Selection";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (!player.hasPermission("magiocore.flyspeed") && !player.isOp()) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cNemáš oprávnění."));
            return true;
        }

        if (args.length > 0) {
            try {
                int speed = Integer.parseInt(args[0]);
                if (speed < 1 || speed > 10) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cRychlost musí být mezi 1 a 10."));
                    return true;
                }
                setFlySpeed(player, speed);
            } catch (NumberFormatException e) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cPoužití: /flyspeed [1-10]"));
            }
        } else {
            openGui(player);
        }

        return true;
    }

    private void openGui(Player player) {
        Inventory inv = Bukkit.createInventory(new FlySpeedGuiHolder(), 18, LegacyComponentSerializer.legacySection().deserialize(title));

        for (int i = 1; i <= 9; i++) {
            inv.setItem(i - 1, createFeather(i));
        }
        inv.setItem(13, createFeather(10));

        player.openInventory(inv);
    }

    private ItemStack createFeather(int speed) {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacySection().deserialize("§bFly Speed " + speed));
        meta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§7Klikni pro nastavení rychlosti létání na " + speed + ".")));
        item.setItemMeta(meta);
        return item;
    }

    private void setFlySpeed(Player player, int speed) {
        // Fly speed in Bukkit is from -1.0 to 1.0. Default is 0.1.
        // We map 1-10 to 0.1-1.0.
        float fSpeed = (float) speed / 10.0f;
        player.setFlySpeed(fSpeed);
        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bTvoje rychlost létání byla nastavena na " + speed + "."));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof FlySpeedGuiHolder)) return;

        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() != Material.FEATHER) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String name = LegacyComponentSerializer.legacySection().serialize(meta.displayName());
        try {
            int speed = Integer.parseInt(name.replaceAll("[^0-9]", ""));
            setFlySpeed(player, speed);
            player.closeInventory();
        } catch (Exception e) {
            // ignore
        }
    }

    private static class FlySpeedGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
