package me.jules.magiocore;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaytimeUtils {

    public static String formatPlaytime(Player player) {
        // PLAY_ONE_MINUTE is actually ticks played
        int ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        long remainingHours = hours % 24;

        return days + "d " + remainingHours + "h";
    }
}
