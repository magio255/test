package me.jules.magiocore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
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
                int timeLeft = s.getInt("timeLeft", 15);
                List<ItemStack> loot = (List<ItemStack>) s.getList("loot", new ArrayList<>());

                spawners.put(loc, new VirtualSpawnerData(loc, type, timeLeft, loot));
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
            }
        }, 20L, 20L);
    }

    private void generateLoot(VirtualSpawnerData data) {
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
        boolean merged = false;
        for (ItemStack lootItem : data.loot) {
            if (lootItem != null && lootItem.isSimilar(item) && lootItem.getAmount() < 64) {
                lootItem.setAmount(lootItem.getAmount() + 1);
                merged = true;
                break;
            }
        }
        if (!merged) {
            data.loot.add(item);
        }
    }

    public void addSpawner(Location loc, EntityType type) {
        spawners.put(loc, new VirtualSpawnerData(loc, type, 15, new ArrayList<>()));
        save();
    }

    public void removeSpawner(Location loc) {
        spawners.remove(loc);
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
    }

    public static class VirtualSpawnerData {
        public Location location;
        public EntityType type;
        public int timeLeft;
        public List<ItemStack> loot;

        public VirtualSpawnerData(Location location, EntityType type, int timeLeft, List<ItemStack> loot) {
            this.location = location;
            this.type = type;
            this.timeLeft = timeLeft;
            this.loot = loot;
        }
    }
}
