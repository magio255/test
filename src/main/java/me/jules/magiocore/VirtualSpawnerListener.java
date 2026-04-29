package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualSpawnerListener implements Listener {
    private final MagioCore plugin;
    private final VirtualSpawnerManager manager;

    public VirtualSpawnerListener(MagioCore plugin, VirtualSpawnerManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey(plugin, "virtual_spawner");
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String typeStr = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            EntityType type = EntityType.valueOf(typeStr);
            manager.addSpawner(event.getBlock().getLocation(), type);
            event.getPlayer().sendMessage(FontUtils.parse("&#00fbff" + "ᴠɪʀᴛᴜáʟɴí sᴘᴀᴡɴᴇʀ ʙʏʟ ᴠʏᴛᴠᴏřᴇɴ."));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        VirtualSpawnerManager.VirtualSpawnerData data = manager.getSpawner(loc);
        if (data != null) {
            manager.removeSpawner(loc);

            ItemStack spawner = new ItemStack(Material.SPAWNER);
            ItemMeta meta = spawner.getItemMeta();
            meta.displayName(FontUtils.parse("&#00fbffᴠɪʀᴛᴜáʟɴí sᴘᴀᴡɴᴇʀ (" + data.type.name() + ")"));
            meta.lore(Collections.singletonList(FontUtils.parse("§7ᴘᴏʟᴏž ᴛᴇɴᴛᴏ sᴘᴀᴡɴᴇʀ ᴘʀᴏ ᴠʏᴛᴠᴏřᴇɴí ᴠɪʀᴛᴜáʟɴíʜᴏ sᴘᴀᴡɴᴇʀᴜ.")));
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "virtual_spawner"), PersistentDataType.STRING, data.type.name());
            spawner.setItemMeta(meta);

            event.setDropItems(false);
            loc.getWorld().dropItemNaturally(loc, spawner);
            event.getPlayer().sendMessage(FontUtils.parse("§c" + "ᴠɪʀᴛᴜáʟɴí sᴘᴀᴡɴᴇʀ ʙʏʟ ᴏᴅsᴛʀᴀɴěɴ."));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.SPAWNER) return;

        VirtualSpawnerManager.VirtualSpawnerData data = manager.getSpawner(block.getLocation());
        if (data != null) {
            event.setCancelled(true);
            openSpawnerGui(event.getPlayer(), data);
        }
    }

    private void openSpawnerGui(Player player, VirtualSpawnerManager.VirtualSpawnerData data) {
        Inventory inv = Bukkit.createInventory(new SpawnerGuiHolder(data), 27, FontUtils.parse("&#00fbffᴠɪʀᴛᴜáʟɴí sᴘᴀᴡɴᴇʀ"));

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, glass);

        updateSpawnerGui(inv, data);
        player.openInventory(inv);

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof SpawnerGuiHolder holder && holder.data == data) {
                updateSpawnerGui(player.getOpenInventory().getTopInventory(), data);
            } else {
                task.cancel();
            }
        }, 20L, 20L);
    }

    private void updateSpawnerGui(Inventory inv, VirtualSpawnerManager.VirtualSpawnerData data) {
        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        meta.displayName(FontUtils.parse("&#00fbffᴜsᴄʜᴏᴠᴀɴý ʟᴏᴏᴛ"));
        meta.lore(Collections.singletonList(FontUtils.parse("§7ᴅᴀʟší sᴘᴀᴡɴ ᴢᴀ: &#00fbff" + data.timeLeft + "s")));
        chest.setItemMeta(meta);
        inv.setItem(13, chest);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SpawnerGuiHolder holder) {
            event.setCancelled(true);
            if (event.getRawSlot() == 13) {
                openLootGui((Player) event.getWhoClicked(), holder.data, 0);
            }
            return;
        }

        if (event.getInventory().getHolder() instanceof LootGuiHolder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();

            if (slot < 45) {
                int itemIndex = holder.page * 45 + slot;
                if (itemIndex < holder.data.loot.size()) {
                    ItemStack item = holder.data.loot.get(itemIndex);
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(item);
                        holder.data.loot.remove(itemIndex);
                        openLootGui(player, holder.data, holder.page);
                        manager.save();
                    } else {
                        player.sendMessage(FontUtils.parse("§c" + "ᴍáš ᴘʟɴý ɪɴᴠᴇɴᴛář."));
                    }
                }
            } else if (slot == 45 && holder.page > 0) {
                openLootGui(player, holder.data, holder.page - 1);
            } else if (slot == 53 && (holder.page + 1) * 45 < holder.data.loot.size()) {
                openLootGui(player, holder.data, holder.page + 1);
            } else if (slot == 48) {
                if (holder.data.loot.isEmpty()) {
                    player.sendMessage(FontUtils.parse("§c" + "ᴢᴅᴇ ɴᴇɴí žáᴅɴý ʟᴏᴏᴛ ᴋ ᴠʏʙʀáɴí."));
                    return;
                }
                for (ItemStack item : new ArrayList<>(holder.data.loot)) {
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(item);
                        holder.data.loot.remove(item);
                    } else {
                        player.sendMessage(FontUtils.parse("§c" + "ɪɴᴠᴇɴᴛář ᴊᴇ ᴘʟɴý! ᴢʙýᴠᴀᴊíᴄí ᴘřᴇᴅᴍěᴛʏ ᴢůsᴛᴀʟʏ ᴠᴇ sᴘᴀᴡɴᴇʀᴜ."));
                        break;
                    }
                }
                openLootGui(player, holder.data, 0);
                manager.save();
            } else if (slot == 50) {
                if (holder.data.loot.isEmpty()) {
                    player.sendMessage(FontUtils.parse("§c" + "ᴢᴅᴇ ɴᴇɴí žáᴅɴý ʟᴏᴏᴛ ᴋ ᴠʏʜᴏᴢᴇɴí."));
                    return;
                }
                for (ItemStack item : holder.data.loot) {
                    holder.data.location.getWorld().dropItemNaturally(holder.data.location.clone().add(0.5, 1, 0.5), item);
                }
                holder.data.loot.clear();
                player.sendMessage(FontUtils.parse("&#00fbff" + "ᴠšᴇᴄʜᴇɴ ʟᴏᴏᴛ ʙʏʟ ᴠʏʜᴏᴢᴇɴ ɴᴀ ᴢᴇᴍ."));
                openLootGui(player, holder.data, 0);
                manager.save();
            }
        }
    }

    private void openLootGui(Player player, VirtualSpawnerManager.VirtualSpawnerData data, int page) {
        Inventory inv = Bukkit.createInventory(new LootGuiHolder(data, page), 54, FontUtils.parse("&#00fbffᴜsᴄʜᴏᴠᴀɴý ʟᴏᴏᴛ"));

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 45; i < 54; i++) inv.setItem(i, glass);

        int start = page * 45;
        for (int i = 0; i < 45 && start + i < data.loot.size(); i++) {
            inv.setItem(i, data.loot.get(start + i));
        }

        if (page > 0) {
            inv.setItem(45, createItem(Material.ARROW, "&#00fbffᴘřᴇᴅᴄʜᴏᴢí sᴛʀᴀɴᴀ"));
        }
        if ((page + 1) * 45 < data.loot.size()) {
            inv.setItem(53, createItem(Material.ARROW, "&#00fbffᴅᴀʟší sᴛʀᴀɴᴀ"));
        }

        inv.setItem(48, createItem(Material.HOPPER, "&#00ff44ᴠʏʙʀᴀᴛ ᴠšᴇᴄʜᴇɴ ʟᴏᴏᴛ", "§7ᴋʟɪᴋɴɪ ᴘʀᴏ ᴠʏʙʀáɴí ᴠšᴇᴄʜ ᴘřᴇᴅᴍěᴛů."));
        inv.setItem(50, createItem(Material.DISPENSER, "&#ffbb00ᴠʏʜᴏᴅɪᴛ ᴠšᴇᴄʜᴇɴ ʟᴏᴏᴛ", "§7ᴋʟɪᴋɴɪ ᴘʀᴏ ᴠʏʜᴏᴢᴇɴí ᴠšᴇᴄʜ ᴘřᴇᴅᴍěᴛů ɴᴀ ᴢᴇᴍ."));

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(FontUtils.parse(name));
        if (lore.length > 0) {
            List<Component> l = new ArrayList<>();
            for (String s : lore) l.add(FontUtils.parse(s));
            meta.lore(l);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static class SpawnerGuiHolder implements InventoryHolder {
        public final VirtualSpawnerManager.VirtualSpawnerData data;
        public SpawnerGuiHolder(VirtualSpawnerManager.VirtualSpawnerData data) { this.data = data; }
        @Override public @NotNull Inventory getInventory() { return null; }
    }

    public static class LootGuiHolder implements InventoryHolder {
        public final VirtualSpawnerManager.VirtualSpawnerData data;
        public final int page;
        public LootGuiHolder(VirtualSpawnerManager.VirtualSpawnerData data, int page) { this.data = data; this.page = page; }
        @Override public @NotNull Inventory getInventory() { return null; }
    }
}
