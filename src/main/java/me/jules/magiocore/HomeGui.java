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

public class HomeGui implements Listener {
    private final MagioCore plugin;
    private final HomeManager homeManager;
    private final String title = "§8Homes";

    public HomeGui(MagioCore plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new HomeGuiHolder(), 36, LegacyComponentSerializer.legacySection().deserialize(title));
        Map<Integer, Home> homes = homeManager.getHomes(player.getUniqueId());
        int maxHomes = PlaytimeUtils.getMaxHomes(player);

        // Background
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.empty());
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 36; i++) {
            inv.setItem(i, glass);
        }

        for (int i = 1; i <= 7; i++) {
            Home home = homes.get(i);
            boolean isLocked = i > maxHomes;

            // Bed (Teleport) - Row 2 (slots 10-16)
            Material bedMaterial = isLocked ? Material.BARRIER : ((home != null) ? Material.BLUE_BED : Material.GREEN_BED);
            String nameColor = isLocked ? "§8" : ((home != null) ? "§e" : "§a");

            ItemStack bed = new ItemStack(bedMaterial);
            ItemMeta bedMeta = bed.getItemMeta();
            bedMeta.displayName(LegacyComponentSerializer.legacySection().deserialize(nameColor + "Domov č. " + i + (isLocked ? " (Zamčeno)" : "")));
            if (isLocked) {
                bedMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§cTvůj limit domovů je: " + maxHomes)));
            } else if (home != null) {
                bedMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§7Klikni pro teleport.")));
            } else {
                bedMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§cDomov není nastaven.")));
            }
            bed.setItemMeta(bedMeta);
            inv.setItem(i + 9, bed);

            // Pearl (Set) - Row 3 (slots 19-25)
            ItemStack pearl = new ItemStack(isLocked ? Material.BARRIER : Material.ENDER_PEARL);
            ItemMeta pearlMeta = pearl.getItemMeta();
            pearlMeta.displayName(LegacyComponentSerializer.legacySection().deserialize(isLocked ? "§8Nastavit domov č. " + i + " (Zamčeno)" : "§bNastavit domov č. " + i));
            if (isLocked) {
                pearlMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§cTvůj limit domovů je: " + maxHomes)));
            } else {
                pearlMeta.lore(List.of(LegacyComponentSerializer.legacySection().deserialize("§7Klikni pro nastavení domova.")));
            }
            pearl.setItemMeta(pearlMeta);
            inv.setItem(i + 18, pearl);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof HomeGuiHolder)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();
        int maxHomes = PlaytimeUtils.getMaxHomes(player);

        if (slot >= 10 && slot <= 16) {
            int homeNum = slot - 9;
            if (homeNum > maxHomes) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cTento slot je zamčený (Tvůj limit: " + maxHomes + ")."));
                return;
            }
            Home home = homeManager.getHome(player.getUniqueId(), homeNum);
            if (home != null) {
                player.closeInventory();
                TeleportUtils.startTeleportCountdown(player, home.getLocation(), plugin, success -> {});
            } else {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cTento domov nemáš nastavený."));
            }
        } else if (slot >= 19 && slot <= 25) {
            int homeNum = slot - 18;
            if (homeNum > maxHomes) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cTento slot je zamčený (Tvůj limit: " + maxHomes + ")."));
                return;
            }
            homeManager.setHome(player.getUniqueId(), homeNum, player.getLocation());
            player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bNastavil jsi si domov č. " + homeNum + "."));
            player.closeInventory();
            open(player); // Refresh
        }
    }

    private static class HomeGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
