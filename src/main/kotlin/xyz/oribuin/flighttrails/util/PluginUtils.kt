package xyz.oribuin.flighttrails.util

import org.bukkit.Color

fun Color.toHex(): String = "#${String.format("%02x%02x%02x", this.red, this.green, this.blue)}"

fun String.fromHex(): Color {
    val decoded = java.awt.Color.decode(this)
    return Color.fromRGB(decoded.red, decoded.green, decoded.blue)
}

fun java.awt.Color.fromAwt(): Color = Color.fromRGB(this.red, this.green, this.blue)