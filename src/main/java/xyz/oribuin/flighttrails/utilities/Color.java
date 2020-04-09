package xyz.oribuin.flighttrails.utilities;

import org.bukkit.ChatColor;

public class Color {
    public static String msg(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
