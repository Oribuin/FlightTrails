package xyz.oribuin.flighttrails.util

import org.bukkit.Color
import xyz.oribuin.flighttrails.FlightTrails

object PluginUtils {

    fun toHex(color: Color): String {
        return "#" + String.format("%02x%02x%02x", color.red, color.green, color.blue)
    }

    fun fromHex(hex: String): Color {
        val decoded = java.awt.Color.decode(hex)
        return Color.fromRGB(decoded.red, decoded.green, decoded.blue)
    }

    fun fromAwtColor(color: java.awt.Color): Color {
        return Color.fromRGB(color.red, color.green, color.blue)
    }

    fun debug(plugin: FlightTrails, msg: String) {
        if (!plugin.config.getBoolean("debug")) return

        plugin.logger.warning("[DEBUGGER] $msg")
    }
}