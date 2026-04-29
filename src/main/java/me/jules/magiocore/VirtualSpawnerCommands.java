package me.jules.magiocore;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VirtualSpawnerCommands implements CommandExecutor, TabCompleter {
    private final MagioCore plugin;
    private final VirtualSpawnerManager manager;

    public VirtualSpawnerCommands(MagioCore plugin, VirtualSpawnerManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("magiocore.virtualspawner")) {
            player.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(FontUtils.parse("&#00fbff" + "ᴘᴏᴜžɪᴛí: /ss <ʟɪsᴛ|ɢɪᴠᴇ>"));
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("list")) {
            player.sendMessage(FontUtils.parse("&#00fbff" + "sᴇᴢɴᴀᴍ ᴠɪʀᴛᴜáʟɴíᴄʜ sᴘᴀᴡɴᴇʀů:"));
            for (VirtualSpawnerManager.VirtualSpawnerData data : manager.getAllSpawners()) {
                String loc = data.location.getWorld().getName() + " " + data.location.getBlockX() + " " + data.location.getBlockY() + " " + data.location.getBlockZ();
                player.sendMessage(FontUtils.parse("§7- &#00fbff" + data.type.name() + " §7ɴᴀ " + loc));
            }
        } else if (sub.equals("give")) {
            if (args.length < 2) {
                player.sendMessage(FontUtils.parse("§c" + "ᴘᴏᴜžɪᴛí: /ss ɢɪᴠᴇ <ᴛʏᴘᴇ> [ᴍɴᴏžsᴛᴠí]"));
                return true;
            }
            try {
                EntityType type = EntityType.valueOf(args[1].toUpperCase());
                int amount = args.length > 2 ? Integer.parseInt(args[2]) : 1;

                ItemStack spawner = new ItemStack(Material.SPAWNER, amount);
                ItemMeta meta = spawner.getItemMeta();

                meta.displayName(FontUtils.parse("&#00fbff&lᴠɪʀᴛᴜáʟɴí sᴘᴀᴡɴᴇʀ"));
                meta.lore(Arrays.asList(
                    FontUtils.parse("§7ᴛʏᴘ: &#00fbff" + type.name()),
                    FontUtils.parse(""),
                    FontUtils.parse("&#00fbff» §7ᴘᴏʟᴏž ᴘʀᴏ ᴠʏᴛᴠᴏřᴇɴí sᴘᴀᴡɴᴇʀᴜ"),
                    FontUtils.parse("&#00fbff» §7ᴋʟɪᴋɴɪ sᴛᴇᴊɴýᴍ ᴛʏᴘᴇᴍ ᴘʀᴏ sᴛᴀᴄᴋᴏᴠáɴí"),
                    FontUtils.parse(""),
                    FontUtils.parse("&#FCD05Cᴅɪsᴘʟᴀʏ &#4498DBꜱᴇʀᴠᴇʀ ꜱʏꜱᴛᴇᴍ")
                ));

                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "virtual_spawner"), PersistentDataType.STRING, type.name());
                spawner.setItemMeta(meta);

                player.getInventory().addItem(spawner);
                player.sendMessage(FontUtils.parse("&#00fbff" + "ᴅᴏsᴛᴀʟ ᴊsɪ " + amount + "x ᴠɪʀᴛᴜáʟɴí sᴘᴀᴡɴᴇʀ " + type.name() + "."));
            } catch (Exception e) {
                player.sendMessage(FontUtils.parse("§c" + "ɴᴇᴘʟᴀᴛɴý ᴛʏᴘ ᴍᴏʙᴀ ɴᴇʙᴏ ᴍɴᴏžsᴛᴠí."));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "give").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Arrays.stream(EntityType.values())
                    .map(EntityType::name)
                    .map(String::toLowerCase)
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return Arrays.asList("1", "10", "64").stream()
                    .filter(s -> s.startsWith(args[2]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
