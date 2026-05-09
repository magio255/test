package me.jules.magiocore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaltopGui implements Listener {
    private final MagioCore plugin;
    private final BaltopManager manager;
    private final String title = "&#EA427F» " + "ʙᴀʟᴛᴏᴘ";
    private final Map<UUID, Integer> playerPages = new HashMap<>();
    private final String goldHeadBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRhMDc0Y2U5MThkNDkyYzNhNjhiMTRmY2FhYWYzZDU1N2RjOTI1NWFhYjMxYWJkZTk0MGI0ZTI0ZDA5In19fQ==";

    public BaltopGui(MagioCore plugin, BaltopManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void open(Player player, int page) {
        Inventory inv = Bukkit.createInventory(new BaltopGuiHolder(), 54, FontUtils.parse(title));
        List<BaltopManager.BaltopEntry> top = manager.getCachedTop();

        int start = (page - 1) * 45;
        if (start >= top.size() && !top.isEmpty()) {
            page = (int) Math.ceil((double) top.size() / 45);
            start = (page - 1) * 45;
        }
        playerPages.put(player.getUniqueId(), page);

        for (int i = 0; i < 45; i++) {
            int index = start + i;
            if (index < top.size()) {
                BaltopManager.BaltopEntry entry = top.get(index);
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();

                // Use a consistent head texture to avoid Mojang API rate limits (403 errors)
                applyTexture(meta, goldHeadBase64);

                meta.displayName(FontUtils.parse("&#ffbb00" + (index + 1) + ". §f" + entry.name()));
                meta.lore(List.of(FontUtils.parse("§7" + "ʙᴀʟᴀɴᴄᴇ" + ": &#00ff44" + FontUtils.formatMoney(entry.balance()) + " $")));
                head.setItemMeta(meta);
                inv.setItem(i, head);
            }
        }

        // Navigation
        inv.setItem(45, createNav("§c" + "ᴢᴘěᴛ", Material.ARROW));
        inv.setItem(49, createNav("&#ffbb00" + "ʜʟᴇᴅᴀᴛ ʜʀáčᴇ", Material.OAK_SIGN));
        inv.setItem(53, createNav("&#00ff44" + "ᴅᴀʟší", Material.ARROW));

        player.openInventory(inv);
    }

    private void applyTexture(SkullMeta meta, String base64) {
        UUID uuid = UUID.nameUUIDFromBytes(base64.getBytes());
        PlayerProfile profile = Bukkit.createProfile(uuid, "BaltopHead");
        PlayerTextures textures = profile.getTextures();

        try {
            String decoded = new String(Base64.getDecoder().decode(base64));
            String urlStr = decoded.substring(decoded.indexOf("http"), decoded.lastIndexOf("\""));
            textures.setSkin(new URL(urlStr));
        } catch (Exception e) {
            // ignore
        }

        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
    }

    private ItemStack createNav(String name, Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(FontUtils.parse(name));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof BaltopGuiHolder)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();
        int page = playerPages.getOrDefault(player.getUniqueId(), 1);

        if (slot == 45) { // Back
            if (page > 1) open(player, page - 1);
        } else if (slot == 53) { // Next
            if (page * 45 < manager.getCachedTop().size()) open(player, page + 1);
        } else if (slot == 49) { // Search
            player.closeInventory();
            player.sendMessage(FontUtils.parse("&#EA427Fʙᴀʟᴛᴏᴘ &#888888» §f" + "ɴᴀᴘɪš ᴊᴍéɴᴏ ʜʀáčᴇ ᴅᴏ ᴄʜᴀᴛᴜ:"));
            plugin.getChatListener().setSearchMode(player.getUniqueId(), true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof BaltopGuiHolder) {
            event.setCancelled(true);
        }
    }

    private static class BaltopGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
