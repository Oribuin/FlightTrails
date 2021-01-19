package xyz.oribuin.flighttrails.util

import org.bukkit.Color

object TrailUtils {

    @JvmStatic
    fun formatToHex(color: Color?): String {
        if (color == null)
            return "#FFFFFF"

        return "#${String.format("%02x%02x%02x", color.red, color.green, color.blue)}"
    }

    @JvmStatic
    fun formatToHex(color: java.awt.Color?): String {
        if (color == null)
            return "#FFFFFF"

        return "#${String.format("%02x%02x%02x", color.red, color.green, color.blue)}"
    }
}