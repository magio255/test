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
    private final String title = "§8» §b" + FontUtils.toSmallCaps("Fly Speed Selection");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (!player.hasPermission("magiocore.flyspeed") && !player.isOp()) {
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§c" + FontUtils.toSmallCaps("No permission.")));
            return true;
        }

        if (args.length > 0) {
            try {
                int speed = Integer.parseInt(args[0]);
                if (speed < 1 || speed > 10) {
                    player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§c" + FontUtils.toSmallCaps("Speed must be 1-10.")));
                    return true;
                }
                setFlySpeed(player, speed);
            } catch (NumberFormatException e) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§c" + FontUtils.toSmallCaps("Usage: /flyspeed [1-10]")));
            }
        } else {
            openGui(player);
        }

        return true;
    }

    private void openGui(Player player) {
        Inventory inv = Bukkit.createInventory(new FlySpeedGuiHolder(), 18, LegacyComponentSerializer.legacySection().deserialize(title));

        // Background
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.empty());
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 18; i++) {
            inv.setItem(i, glass);
        }

        for (int i = 1; i <= 9; i++) {
            inv.setItem(i - 1, createFeather(i));
        }
        inv.setItem(13, createFeather(10));

        player.openInventory(inv);
    }

    private ItemStack createFeather(int speed) {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacySection().deserialize("§b" + FontUtils.toSmallCaps("Fly Speed " + speed)));
        meta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§7" + FontUtils.toSmallCaps("Click to set speed to " + speed))));
        item.setItemMeta(meta);
        return item;
    }

    private void setFlySpeed(Player player, int speed) {
        float fSpeed = (float) speed / 10.0f;
        player.setFlySpeed(fSpeed);
        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§b" + FontUtils.toSmallCaps("Fly speed set to " + speed + ".")));
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
