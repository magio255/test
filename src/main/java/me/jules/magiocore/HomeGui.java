package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import java.util.Map;

public class HomeGui implements Listener, InventoryHolder {
    private final MagioCore plugin;
    private final HomeManager homeManager;
    private final String title = "§8Homes";

    public HomeGui(MagioCore plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new HomeGuiHolder(), 27, LegacyComponentSerializer.legacySection().deserialize(title));
        Map<Integer, Home> homes = homeManager.getHomes(player.getUniqueId());

        for (int i = 1; i <= 7; i++) {
            Home home = homes.get(i);

            // Bed (Teleport)
            Material bedMaterial = (home != null) ? Material.BLUE_BED : Material.GREEN_BED;
            String nameColor = (home != null) ? "§e" : "§a";

            ItemStack bed = new ItemStack(bedMaterial);
            ItemMeta bedMeta = bed.getItemMeta();
            bedMeta.displayName(LegacyComponentSerializer.legacySection().deserialize(nameColor + "Domov č. " + i));
            if (home != null) {
                bedMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§7Klikni pro teleport.")));
            } else {
                bedMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§cDomov není nastaven.")));
            }
            bed.setItemMeta(bedMeta);
            inv.setItem(i, bed);

            // Pearl (Set)
            ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
            ItemMeta pearlMeta = pearl.getItemMeta();
            pearlMeta.displayName(LegacyComponentSerializer.legacySection().deserialize("§bNastavit domov č. " + i));
            pearlMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§7Klikni pro nastavení domova.")));
            pearl.setItemMeta(pearlMeta);
            inv.setItem(i + 9, pearl);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof HomeGuiHolder)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        if (slot >= 1 && slot <= 7) {
            int homeNum = slot;
            Home home = homeManager.getHome(player.getUniqueId(), homeNum);
            if (home != null) {
                player.closeInventory();
                TeleportUtils.startTeleportCountdown(player, home.getLocation(), 3, plugin, success -> {});
            } else {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cTento domov nemáš nastavený."));
            }
        } else if (slot >= 10 && slot <= 16) {
            int homeNum = slot - 9;
            homeManager.setHome(player.getUniqueId(), homeNum, player.getLocation());
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bNastavil jsi si domov č. " + homeNum + "."));
            player.closeInventory();
            open(player); // Refresh
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null; // Not used as we use custom holder
    }

    private static class HomeGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
