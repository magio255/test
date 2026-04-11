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
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CoinflipGui implements Listener {
    private final MagioCore plugin;
    private final CoinflipManager manager;
    private final String title = "§bCoinflip Menu";

    public CoinflipGui(MagioCore plugin, CoinflipManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(new CoinflipGuiHolder(), 54, LegacyComponentSerializer.legacySection().deserialize(title));

        List<CoinflipManager.CoinflipBet> bets = manager.getActiveBets();
        for (int i = 0; i < bets.size() && i < 54; i++) {
            CoinflipManager.CoinflipBet bet = bets.get(i);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(bet.creator));
            meta.displayName(LegacyComponentSerializer.legacySection().deserialize("§bHráč: §f" + bet.creatorName));
            meta.lore(List.of(
                LegacyComponentSerializer.legacySection().deserialize("§7Sázka: §f" + bet.amount + " $"),
                LegacyComponentSerializer.legacySection().deserialize(""),
                LegacyComponentSerializer.legacySection().deserialize("§aKlikni pro sázku!")
            ));
            head.setItemMeta(meta);
            inv.setItem(i, head);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof CoinflipGuiHolder)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();
        List<CoinflipManager.CoinflipBet> bets = manager.getActiveBets();

        if (slot >= 0 && slot < bets.size()) {
            CoinflipManager.CoinflipBet bet = bets.get(slot);
            if (bet.creator.equals(player.getUniqueId())) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cNemůžeš hrát proti sobě."));
                return;
            }

            if (plugin.getEconomy().getBalance(player) < bet.amount) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cNemáš dostatek peněz na tuto sázku."));
                return;
            }

            plugin.getEconomy().withdrawPlayer(player, bet.amount);
            manager.removeBet(bet);
            player.closeInventory();
            new CoinflipAnimation(plugin, Bukkit.getPlayer(bet.creator), player, bet.amount).start();
        }
    }

    private static class CoinflipGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
