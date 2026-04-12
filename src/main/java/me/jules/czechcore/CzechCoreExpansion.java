package me.jules.czechcore;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CzechCoreExpansion extends PlaceholderExpansion {

    private final CzechCore plugin;

    public CzechCoreExpansion(CzechCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "czechcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Jules";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("playtime")) {
            return PlaytimeUtils.formatPlaytime(player);
        }

        if (params.equalsIgnoreCase("max_homes")) {
            return String.valueOf(PlaytimeUtils.getMaxHomes(player));
        }

        return null;
    }
}
