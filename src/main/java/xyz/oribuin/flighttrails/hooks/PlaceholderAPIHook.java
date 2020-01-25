package xyz.oribuin.flighttrails.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.oribuin.flighttrails.FlightTrails;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook {

    FlightTrails plugin;

    private static Boolean enabled;

    public PlaceholderAPIHook(FlightTrails plugin) {
        this.plugin = plugin;
    }

    public static boolean enabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static String applyPlaceholders(Player player, String text) {
        if (enabled())
            return PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }
}