package me.jules.magiocore.modules;

import me.jules.magiocore.FontUtils;
import me.jules.magiocore.MagioCore;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class KeyAllModule implements CommandExecutor {
    private final MagioCore plugin;

    public KeyAllModule(MagioCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("admin.keyall")) {
            sender.sendMessage(FontUtils.parse("§c" + "ɴᴀ ᴛᴏʜʟᴇ ɴᴇᴍáš ᴘʀáᴠᴀ!"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(FontUtils.parse("&#888888" + "ᴘᴏᴜžɪᴛí: &f/ᴋᴇʏᴀʟʟ <ᴍᴏɴᴇʏ|ᴋᴇʏ|sᴘᴀᴡɴᴇʀ|ᴀɴɪᴍᴀʟ>"));
            return true;
        }

        String type = args[0].toLowerCase();
        switch (type) {
            case "money":
                startKeyAll("&#FCD05C&lᴍᴏɴᴇʏ ᴋᴇʏᴀʟʟ ʙʏʟ sᴘᴜšᴛěɴ", "§7Všichni obdrží peníze!", "Money", Particle.TOTEM_OF_UNDYING, Particle.FLAME, Particle.GLOW, "money giveall " + plugin.getConfig().getInt("keyall.money-amount", 30000));
                break;
            case "key":
                startKeyAll("&#5CE0FC&lᴋᴇʏ ᴋᴇʏᴀʟʟ ʙʏʟ sᴘᴜšᴛěɴ", "§7Všichni obdrží klíče!", "Key", Particle.SOUL_FIRE_FLAME, Particle.ENCHANT, Particle.WAX_OFF, "crate giveall vote " + plugin.getConfig().getInt("keyall.vote-key-amount", 1));
                break;
            case "spawner":
                startKeyAll("&#FC5C5C&lspawner ᴋᴇʏᴀʟʟ ʙʏʟ sᴘᴜšᴛěɴ", "§7Všichni obdrží spawner!", "Spawner", Particle.LAVA, Particle.LARGE_SMOKE, Particle.FLAME, "ss give @a skeleton 1");
                break;
            case "animal":
                startKeyAll("&#A4FC5C&lᴀɴɪᴍᴀʟ ᴋᴇʏᴀʟʟ ʙʏʟ sᴘᴜšᴛěɴ", "§7Všichni obdrží zvířátka!", "Animal", Particle.HAPPY_VILLAGER, Particle.HEART, Particle.COMPOSTER, "give @a camel_spawn_egg 1", "give @a lead 1", "give @a oak_fence 1", "give @a name_tag 1");
                break;
            default:
                sender.sendMessage(FontUtils.parse("&#888888" + "ᴘᴏᴜžɪᴛí: &f/ᴋᴇʏᴀʟʟ <ᴍᴏɴᴇʏ|ᴋᴇʏ|sᴘᴀᴡɴᴇʀ|ᴀɴɪᴍᴀʟ>"));
                break;
        }

        return true;
    }

    private void startKeyAll(String title, String subtitle, String prefix, Particle p1, Particle p2, Particle p3, String... commands) {
        String mainTitle = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(FontUtils.parse(title));
        String subTitle = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(FontUtils.parse(subtitle));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(mainTitle, subTitle, 10, 60, 10);
            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 1, true, false));
        }

        new BukkitRunnable() {
            int step = 3;

            @Override
            public void run() {
                if (step > 0) {
                    Bukkit.broadcast(FontUtils.parse("&#888888[&6&lᴋᴇʏᴀʟʟ&r&#888888] §7" + prefix + " ᴋᴇʏᴀʟʟ ᴢᴀ &#f1c40f" + step + "..."));
                    Particle currentParticle = (step == 3) ? p1 : (step == 2) ? p2 : p3;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.getWorld().spawnParticle(currentParticle, p.getLocation().add(0, 1, 0), 100, 0.5, 0.5, 0.5, 0.1);
                    }
                    step--;
                } else {
                    for (String cmd : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, p.getLocation().add(0, 1, 0), 1, 0, 0, 0, 0);
                    }
                    String rewardMsg = switch (prefix) {
                        case "Money" -> "§7Všichni dostali &#f1c40f" + plugin.getConfig().getInt("keyall.money-amount", 30000) + "$!";
                        case "Key" -> "§7Všichni dostali &#00fbffᴋʟíč!";
                        case "Spawner" -> "§7Všichni dostali &#ff0000sᴘᴀᴡɴᴇʀ!";
                        case "Animal" -> "§7Všichni dostali velblouda a výbavu s jmenovkou!";
                        default -> "";
                    };
                    Bukkit.broadcast(FontUtils.parse("&#888888[&6&lᴋᴇʏᴀʟʟ&r&#888888] &#00ff44&lʙᴜᴍ! " + rewardMsg));
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
