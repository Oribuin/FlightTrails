package xyz.oribuin.flighttrails.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PAPIHook {
    private var enabled: Boolean? = null

    // return true if PlaceholderAPI is enabled
    fun enabled(): Boolean {
        return enabled ?: (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null.also { enabled = it })
    }

    // Apply PlaceholderAPI Placeholders
    fun apply(player: Player?, text: String): String {
        return if (enabled()) PlaceholderAPI.setPlaceholders(player, text) else text
    }
}
