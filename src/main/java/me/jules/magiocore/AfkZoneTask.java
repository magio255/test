package me.jules.magiocore;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AfkZoneTask extends BukkitRunnable {
    private final MagioCore plugin;

    public AfkZoneTask(MagioCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        String regionName = plugin.getConfig().getString("afk-zone.region-name", "afk");
        String command = plugin.getConfig().getString("afk-zone.reward-command");

        if (command == null || command.isEmpty()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInRegion(player, regionName)) {
                String finalCmd = command.replace("%player%", player.getName());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
                });
            }
        }
    }

    private boolean isInRegion(Player player, String regionName) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return false;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }
        return false;
    }
}
