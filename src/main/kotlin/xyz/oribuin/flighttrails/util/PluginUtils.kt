package xyz.oribuin.flighttrails.util

import org.bukkit.Color

object PluginUtils {

    fun toHex(color: Color): String {
        return "#" + String.format("%02x%02x%02x", color.red, color.green, color.blue)
    }

    fun fromHex(hex: String): Color {
        val decoded = java.awt.Color.decode(hex)
        return Color.fromRGB(decoded.red, decoded.green, decoded.blue)
    }

}