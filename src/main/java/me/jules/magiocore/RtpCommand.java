package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RtpCommand implements CommandExecutor, Listener {
    private final MagioCore plugin;
    private final String title = "§aOverworld";
    private final Random random = new Random();

    public RtpCommand(MagioCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        openGui(player);
        return true;
    }

    public void openGui(Player player) {
        Inventory inv = Bukkit.createInventory(new RtpGuiHolder(), 27, LegacyComponentSerializer.legacySection().deserialize(title));

        // Background - Use a darker glass for contrast like in the image (or keep cyan if preferred, but image looks dark)
        // I will use Black Stained Glass Pane for a darker look matching the screenshot's vibe.
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Component.empty());
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, glass);
        }

        // RTP Button (Player Head with Overworld style)
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.displayName(LegacyComponentSerializer.legacySection().deserialize("§aOverworld"));
        headMeta.lore(List.of(
            LegacyComponentSerializer.legacySection().deserialize("§7Klikni zde pro náhodnou teleportaci"),
            LegacyComponentSerializer.legacySection().deserialize("§7někam do světa overworld."),
            Component.empty(),
            LegacyComponentSerializer.legacySection().deserialize("§a➥ KLIKNI PRO TELEPORTACI")
        ));
        // Use a generic earth/overworld head if possible via base64, but for now standard player head is safer.
        // I'll leave it as is, it will be the player's head.
        head.setItemMeta(headMeta);
        inv.setItem(13, head);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof RtpGuiHolder)) return;

        event.setCancelled(true);
        if (event.getRawSlot() == 13) {
            player.closeInventory();
            findRandomLocation(player);
        }
    }

    private void findRandomLocation(Player player) {
        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§bHledám bezpečné místo..."));

        World world = player.getWorld();
        int radius = 5000;

        // Try 10 times to find a safe location
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            Material block = world.getBlockAt(x, y, z).getType();

            if (block != Material.LAVA && block != Material.WATER && block != Material.AIR) {
                TeleportUtils.startTeleportCountdown(player, loc, 3, plugin, success -> {});
                return;
            }
        }

        player.sendMessage(LegacyComponentSerializer.legacySection().deserialize("§cNepodařilo se najít bezpečné místo, zkus to znovu."));
    }

    private static class RtpGuiHolder implements InventoryHolder {
        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }
}
