package xyz.oribuin.flighttrails.hook

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.obj.TrailOptions
import xyz.oribuin.flighttrails.util.PluginUtils

class Expansion(private val plugin: FlightTrails) : PlaceholderExpansion() {

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        val trail = this.plugin.getManager(DataManager::class.java).getTrailOptions(player, false) ?: TrailOptions(player.uniqueId)

        when (params) {
            "particle" -> return trail.particle.name
            "color", "colour" -> return PluginUtils.toHex(trail.particleColor)
            "block" -> return trail.blockData.name
            "item" -> return trail.itemData.type.name
            "enabled" -> return trail.enabled.toString()
            "note" -> return trail.note.toString()
        }

        return null
    }

    override fun getAuthor(): String {
        return this.plugin.description.authors[0]
    }

    override fun getIdentifier(): String {
        return this.plugin.description.name.toLowerCase()
    }

    override fun getVersion(): String {
        return this.plugin.description.version
    }

}