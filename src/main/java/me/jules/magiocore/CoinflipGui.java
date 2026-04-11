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
        Inventory inv = Bukkit.createInventory(new CoinflipGuiHolder(), 36, LegacyComponentSerializer.legacySection().deserialize(title));

        // Background for empty slots
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.empty());
        glass.setItemMeta(glassMeta);
        for (int i = 27; i < 36; i++) {
            inv.setItem(i, glass);
        }

        List<CoinflipManager.CoinflipBet> bets = manager.getActiveBets();
        for (int i = 0; i < bets.size() && i < 27; i++) {
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

        // Tutorial Book
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(LegacyComponentSerializer.legacySection().deserialize("§e§lJak vytvořit Coinflip?"));
        bookMeta.lore(List.of(
            LegacyComponentSerializer.legacySection().deserialize("§7Příkaz: §f/cf <částka>"),
            LegacyComponentSerializer.legacySection().deserialize("§7Příklad: §f/cf 1000"),
            LegacyComponentSerializer.legacySection().deserialize(""),
            LegacyComponentSerializer.legacySection().deserialize("§7Tvůj coinflip se pak zobrazí"),
            LegacyComponentSerializer.legacySection().deserialize("§7zde v tomto menu pro ostatní.")
        ));
        book.setItemMeta(bookMeta);
        inv.setItem(31, book);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof CoinflipGuiHolder)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();
        List<CoinflipManager.CoinflipBet> bets = manager.getActiveBets();

        if (slot >= 0 && slot < bets.size() && slot < 27) {
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
