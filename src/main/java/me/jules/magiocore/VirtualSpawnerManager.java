package me.jules.magiocore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualSpawnerManager {
    private final MagioCore plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<Location, VirtualSpawnerData> spawners = new ConcurrentHashMap<>();
    private BukkitTask task;

    public VirtualSpawnerManager(MagioCore plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "spawners.yml");
        load();
        startTask();
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        spawners.clear();

        ConfigurationSection section = config.getConfigurationSection("spawners");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection s = section.getConfigurationSection(key);
                if (s == null) continue;

                Location loc = s.getLocation("location");
                if (loc == null) continue;

                EntityType type = EntityType.valueOf(s.getString("type", "ZOMBIE"));
                int count = s.getInt("count", 1);
                int timeLeft = s.getInt("timeLeft", 15);
                List<ItemStack> loot = (List<ItemStack>) s.getList("loot", new ArrayList<>());

                spawners.put(loc, new VirtualSpawnerData(loc, type, count, timeLeft, loot));
            }
        }
    }

    public void save() {
        config.set("spawners", null);
        int i = 0;
        for (VirtualSpawnerData data : spawners.values()) {
            String path = "spawners.s" + i++;
            config.set(path + ".location", data.location);
            config.set(path + ".type", data.type.name());
            config.set(path + ".count", data.count);
            config.set(path + ".timeLeft", data.timeLeft);
            config.set(path + ".loot", data.loot);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTask() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (VirtualSpawnerData data : spawners.values()) {
                data.timeLeft--;
                if (data.timeLeft <= 0) {
                    data.timeLeft = 15;
                    generateLoot(data);
                }
                updateHologram(data);
            }
        }, 20L, 20L);
    }

    private void updateHologram(VirtualSpawnerData data) {
        if (data.hologram == null || !data.hologram.isValid()) {
            Location loc = data.location.clone().add(0.5, 1.5, 0.5);
            data.hologram = data.location.getWorld().spawn(loc, TextDisplay.class);
            data.hologram.setBillboard(TextDisplay.Billboard.CENTER);
            data.hologram.setShadowed(true);
            data.hologram.setBackgroundColor(org.bukkit.Color.fromARGB(0, 0, 0, 0));
        }

        int lootCount = data.loot.stream().mapToInt(ItemStack::getAmount).sum();
        String text = "&#00fbff&l" + data.type.name() + " sᴘᴀᴡɴᴇʀ §8(x" + data.count + ")\n" +
                     "&7ꜱᴇʀᴠᴇʀ ᴠɪʀᴛᴜᴀʟ ꜱʏꜱᴛᴇᴍ\n" +
                     "&r\n" +
                     "&fᴘᴏčᴇᴛ ᴘřᴇᴅᴍěᴛů: &#00fbff" + lootCount + " ᴋs\n" +
                     "&fᴅᴀʟší sᴘᴀᴡɴ ᴢᴀ: &#00fbff" + data.timeLeft + "s\n" +
                     "&r\n" +
                     "&#FCD05C⬇ &#4498DBᴋʟɪᴋɴɪ ᴘʀᴏ ᴍᴇɴᴜ &#FCD05C⬇";
        data.hologram.text(FontUtils.parse(text));
    }

    private void generateLoot(VirtualSpawnerData data) {
        int amount = data.count;
        ItemStack item;
        switch (data.type) {
            case ZOMBIE -> item = new ItemStack(Material.ROTTEN_FLESH);
            case SKELETON -> item = new ItemStack(Material.BONE);
            case SPIDER -> item = new ItemStack(Material.STRING);
            case CREEPER -> item = new ItemStack(Material.GUNPOWDER);
            case PIG -> item = new ItemStack(Material.PORKCHOP);
            case COW -> item = new ItemStack(Material.BEEF);
            case CHICKEN -> item = new ItemStack(Material.CHICKEN);
            default -> item = new ItemStack(Material.IRON_INGOT);
        }

        // Add to loot list, merging if possible
        while (amount > 0) {
            int toAdd = Math.min(amount, 64);
            ItemStack stack = item.clone();
            stack.setAmount(toAdd);

            boolean merged = false;
            for (ItemStack lootItem : data.loot) {
                if (lootItem != null && lootItem.isSimilar(stack) && lootItem.getAmount() < 64) {
                    int canAdd = 64 - lootItem.getAmount();
                    int adding = Math.min(toAdd, canAdd);
                    lootItem.setAmount(lootItem.getAmount() + adding);
                    toAdd -= adding;
                    if (toAdd <= 0) {
                        merged = true;
                        break;
                    }
                }
            }
            if (!merged && toAdd > 0) {
                ItemStack finalStack = item.clone();
                finalStack.setAmount(toAdd);
                data.loot.add(finalStack);
                toAdd = 0;
            }
            amount = toAdd;
        }
    }

    public void addSpawner(Location loc, EntityType type) {
        VirtualSpawnerData data = new VirtualSpawnerData(loc, type, 1, 15, new ArrayList<>());
        spawners.put(loc, data);
        updateHologram(data);
        save();
    }

    public void removeSpawner(Location loc) {
        VirtualSpawnerData data = spawners.remove(loc);
        if (data != null && data.hologram != null) {
            data.hologram.remove();
        }
        save();
    }

    public VirtualSpawnerData getSpawner(Location loc) {
        return spawners.get(loc);
    }

    public Collection<VirtualSpawnerData> getAllSpawners() {
        return spawners.values();
    }

    public void stopTask() {
        if (task != null) task.cancel();
        for (VirtualSpawnerData data : spawners.values()) {
            if (data.hologram != null) data.hologram.remove();
        }
    }

    public static class VirtualSpawnerData {
        public Location location;
        public EntityType type;
        public int count;
        public int timeLeft;
        public List<ItemStack> loot;
        public TextDisplay hologram;

        public VirtualSpawnerData(Location location, EntityType type, int count, int timeLeft, List<ItemStack> loot) {
            this.location = location;
            this.type = type;
            this.count = count;
            this.timeLeft = timeLeft;
            this.loot = loot;
        }
    }
}
