package me.jules.czechcore;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class RtpCommand implements CommandExecutor, Listener {
    private final CzechCore plugin;
    private final Random random = new Random();

    public RtpCommand(CzechCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        openGui(player);
        return true;
    }

    public void openGui(Player player) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("rtp.gui");
        if (config == null) return;

        String titleStr = config.getString("title", "Overworld");
        int rows = config.getInt("rows", 3);
        Inventory inv = Bukkit.createInventory(new RtpGuiHolder(), rows * 9, FontUtils.parse("§8» §b" + titleStr));

        // Background - Improved with Border
        Material bgMaterial;
        try {
            bgMaterial = Material.valueOf(config.getString("background", "BLACK_STAINED_GLASS_PANE"));
        } catch (IllegalArgumentException e) {
            bgMaterial = Material.BLACK_STAINED_GLASS_PANE;
        }

        ItemStack glass = new ItemStack(bgMaterial);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.empty());
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        // RTP Button
        ConfigurationSection itemConfig = config.getConfigurationSection("item");
        if (itemConfig != null) {
            Material material;
            try {
                material = Material.valueOf(itemConfig.getString("material", "PLAYER_HEAD"));
            } catch (IllegalArgumentException e) {
                material = Material.PLAYER_HEAD;
            }
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            meta.displayName(FontUtils.parse("§b" + itemConfig.getString("name", "Overworld")));
            List<String> lore = itemConfig.getStringList("lore");
            meta.lore(lore.stream().map(l -> FontUtils.parse(l)).collect(Collectors.toList()));

            if (material == Material.PLAYER_HEAD && itemConfig.contains("texture")) {
                applyTexture((SkullMeta) meta, itemConfig.getString("texture"));
            }

            item.setItemMeta(meta);
            inv.setItem(itemConfig.getInt("slot", 13), item);
        }

        player.openInventory(inv);
    }

    private void applyTexture(SkullMeta meta, String base64) {
        UUID uuid = UUID.nameUUIDFromBytes(base64.getBytes());
        PlayerProfile profile = Bukkit.createProfile(uuid, "CustomHead");
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof RtpGuiHolder)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();
        int rtpSlot = plugin.getConfig().getInt("rtp.gui.item.slot", 13);

        if (slot == rtpSlot) {
            player.closeInventory();
            findRandomLocation(player);
        }
    }

    private void findRandomLocation(Player player) {
        player.sendMessage(FontUtils.parse("§b" + "Finding safe location..."));

        World world = player.getWorld();
        int radius = plugin.getConfig().getInt("rtp.settings.radius", 5000);
        int maxAttempts = plugin.getConfig().getInt("rtp.settings.max-attempts", 10);

        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            Material block = world.getBlockAt(x, y, z).getType();

            if (block != Material.LAVA && block != Material.WATER && block != Material.AIR) {
                TeleportUtils.startTeleportCountdown(player, loc, plugin, success -> {});
                return;
            }
        }

        player.sendMessage(FontUtils.parse("§c" + "Failed to find safe location, try again."));
    }

    private static class RtpGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
