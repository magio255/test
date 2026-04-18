package me.jules.magiocore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UtilityCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "broadcast" -> {
                if (!sender.hasPermission("magiocore.broadcast")) {
                    sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                if (args.length == 0) return false;
                String message = String.join(" ", args);
                Bukkit.broadcast(FontUtils.parse("&#ffbb00ᴏᴢɴáᴍᴇɴí &#888888» §f" + message));
            }
            case "feed" -> {
                if (!sender.hasPermission("magiocore.feed")) {
                    sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                Player target = (args.length > 0) ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
                if (target == null) {
                    sender.sendMessage(FontUtils.parse("§c" + "ʜʀáč ɴᴇʙʏʟ ɴᴀʟᴇᴢᴇɴ."));
                    return true;
                }
                target.setFoodLevel(20);
                target.setSaturation(20);
                sender.sendMessage(FontUtils.parse("&#00fbff" + "ɴᴀsʏᴄᴇɴí ʜʀáčᴇ " + target.getName() + " ʙʏʟᴏ ᴅᴏᴘʟɴěɴᴏ."));
            }
            case "fly" -> {
                if (!sender.hasPermission("magiocore.fly")) {
                    sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                Player target;
                boolean toggle;
                if (args.length == 0) {
                    if (!(sender instanceof Player p)) return true;
                    target = p;
                    toggle = !target.getAllowFlight();
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("on")) {
                        if (!(sender instanceof Player p)) return true;
                        target = p; toggle = true;
                    } else if (args[0].equalsIgnoreCase("off")) {
                        if (!(sender instanceof Player p)) return true;
                        target = p; toggle = false;
                    } else {
                        target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(FontUtils.parse("§c" + "ʜʀáč ɴᴇʙʏʟ ɴᴀʟᴇᴢᴇɴ."));
                            return true;
                        }
                        toggle = !target.getAllowFlight();
                    }
                } else {
                    target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(FontUtils.parse("§c" + "ʜʀáč ɴᴇʙʏʟ ɴᴀʟᴇᴢᴇɴ."));
                        return true;
                    }
                    toggle = args[1].equalsIgnoreCase("on");
                }
                target.setAllowFlight(toggle);
                sender.sendMessage(FontUtils.parse("&#00fbff" + "ʟéᴛáɴí ᴘʀᴏ " + target.getName() + " ʙʏʟᴏ " + (toggle ? "ᴢᴀᴘɴᴜᴛᴏ" : "ᴠʏᴘɴᴜᴛᴏ") + "."));
            }
            case "hat" -> {
                if (!(sender instanceof Player player)) return true;
                if (!player.hasPermission("magiocore.hat")) {
                    player.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                if (args.length > 0 && args[0].equalsIgnoreCase("remove")) {
                    player.getInventory().setHelmet(null);
                    player.sendMessage(FontUtils.parse("&#00fbff" + "ᴘřᴇᴅᴍěᴛ ᴢ ʜʟᴀᴠʏ ʙʏʟ ᴏᴅsᴛʀᴀɴěɴ."));
                    return true;
                }
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand.getType().isAir()) {
                    player.sendMessage(FontUtils.parse("§c" + "ᴍᴜsíš ᴅʀžᴇᴛ ᴘřᴇᴅᴍěᴛ ᴠ ʀᴜᴄᴇ."));
                    return true;
                }
                ItemStack head = player.getInventory().getHelmet();
                player.getInventory().setHelmet(hand);
                player.getInventory().setItemInMainHand(head);
                player.sendMessage(FontUtils.parse("&#00fbff" + "ɴʏɴí ᴍáš ɴᴀ ʜʟᴀᴠě sᴠůj ᴘřᴇᴅᴍěᴛ."));
            }
            case "heal" -> {
                if (!sender.hasPermission("magiocore.heal")) {
                    sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                Player target = (args.length > 0) ? Bukkit.getPlayer(args[0]) : (sender instanceof Player ? (Player) sender : null);
                if (target == null) {
                    sender.sendMessage(FontUtils.parse("§c" + "ʜʀáč ɴᴇʙʏʟ ɴᴀʟᴇᴢᴇɴ."));
                    return true;
                }
                target.setHealth(target.getMaxHealth());
                target.setFoodLevel(20);
                target.setFireTicks(0);
                target.getActivePotionEffects().forEach(effect -> target.removePotionEffect(effect.getType()));
                sender.sendMessage(FontUtils.parse("&#00fbff" + "ʜʀáč " + target.getName() + " ʙʏʟ ᴠʏʟéčᴇɴ."));
            }
            case "repair" -> {
                if (!(sender instanceof Player player)) return true;
                if (!player.hasPermission("magiocore.repair")) {
                    player.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
                    for (ItemStack item : player.getInventory().getContents()) {
                        repairItem(item);
                    }
                    player.sendMessage(FontUtils.parse("&#00fbff" + "ᴠšᴇᴄʜɴʏ ᴘřᴇᴅᴍěᴛʏ ʙʏʟʏ ᴏᴘʀᴀᴠᴇɴʏ."));
                } else {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (repairItem(item)) {
                        player.sendMessage(FontUtils.parse("&#00fbff" + "ᴘřᴇᴅᴍěᴛ ᴠ ʀᴜᴄᴇ ʙʏʟ ᴏᴘʀᴀᴠᴇɴ."));
                    } else {
                        player.sendMessage(FontUtils.parse("§c" + "ᴛᴇɴᴛᴏ ᴘřᴇᴅᴍěᴛ ɴᴇʟᴢᴇ ᴏᴘʀᴀᴠɪᴛ."));
                    }
                }
            }
            case "suicide" -> {
                if (!(sender instanceof Player player)) return true;
                player.setHealth(0);
                player.sendMessage(FontUtils.parse("&#00fbff" + "ʀᴏᴢʜᴏᴅʟ sᴇs ᴜᴋᴏɴčɪᴛ sᴠůj žɪᴠᴏᴛ."));
            }
            case "ptime" -> {
                if (!sender.hasPermission("magiocore.ptime")) {
                    sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                if (args.length == 0) return false;
                Player target = (sender instanceof Player p) ? p : null;
                if (args.length > 1) {
                    target = Bukkit.getPlayer(args[1]);
                }
                if (target == null) {
                    sender.sendMessage(FontUtils.parse("§c" + "ʜʀáč ɴᴇʙʏʟ ɴᴀʟᴇᴢᴇɴ."));
                    return true;
                }
                String timeArg = args[0].toLowerCase();
                boolean fixed = timeArg.startsWith("@");
                if (fixed) timeArg = timeArg.substring(1);

                long time;
                switch (timeArg) {
                    case "day" -> time = 1000;
                    case "night" -> time = 13000;
                    case "dawn" -> time = 23000;
                    case "reset" -> {
                        target.resetPlayerTime();
                        sender.sendMessage(FontUtils.parse("&#00fbff" + "čᴀs ᴘʀᴏ " + target.getName() + " ʙʏʟ ʀᴇsᴇᴛᴏᴠáɴ."));
                        return true;
                    }
                    default -> {
                        try {
                            if (timeArg.endsWith("ticks")) {
                                time = Long.parseLong(timeArg.replace("ticks", ""));
                            } else {
                                time = Long.parseLong(timeArg); // Simplified
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴘʟᴀᴛɴý ꜰᴏʀᴍáᴛ čᴀsᴜ."));
                            return true;
                        }
                    }
                }
                target.setPlayerTime(time, !fixed);
                sender.sendMessage(FontUtils.parse("&#00fbff" + "čᴀs ᴘʀᴏ " + target.getName() + " ɴᴀsᴛᴀᴠᴇɴ ɴᴀ " + timeArg + "."));
            }
            case "pweather" -> {
                if (!sender.hasPermission("magiocore.pweather")) {
                    sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴍáš ᴘřísᴛᴜᴘ ᴋ ᴛᴏᴍᴜᴛᴏ ᴘříᴋᴀᴢᴜ."));
                    return true;
                }
                if (args.length == 0) return false;
                Player target = (sender instanceof Player p) ? p : null;
                if (args.length > 1) {
                    target = Bukkit.getPlayer(args[1]);
                }
                if (target == null) {
                    sender.sendMessage(FontUtils.parse("§c" + "ʜʀáč ɴᴇʙʏʟ ɴᴀʟᴇᴢᴇɴ."));
                    return true;
                }
                switch (args[0].toLowerCase()) {
                    case "sun", "clear" -> target.setPlayerWeather(WeatherType.CLEAR);
                    case "storm" -> target.setPlayerWeather(WeatherType.DOWNFALL);
                    case "reset" -> target.resetPlayerWeather();
                    default -> {
                        sender.sendMessage(FontUtils.parse("§c" + "ɴᴇᴘʟᴀᴛɴý ᴛʏᴘ ᴘᴏčᴀsí."));
                        return true;
                    }
                }
                sender.sendMessage(FontUtils.parse("&#00fbff" + "ᴘᴏčᴀsí ᴘʀᴏ " + target.getName() + " ʙʏʟᴏ ᴢᴍěɴěɴᴏ."));
            }
        }
        return true;
    }

    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(0);
            item.setItemMeta(meta);
            return true;
        }
        return false;
    }
}
