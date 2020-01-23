package me.oribuin.flighttrails.hooks;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.cmds.CmdSetColor;
import me.oribuin.flighttrails.handlers.FlyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
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