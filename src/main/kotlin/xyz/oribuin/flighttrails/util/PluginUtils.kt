package xyz.oribuin.flighttrails.util

import org.bukkit.Color

object PluginUtils {

    fun toHex(color: Color): String {
        return "#" + String.format("%02x%02x%02x", color.red, color.green, color.blue)
    }

    fun fromHex(hex: String): Color {
        return Color.fromRGB(java.awt.Color.decode(hex).rgb)
    }

}