package xyz.oribuin.flighttrails.manager

import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.obj.TrailOptions
import xyz.oribuin.flighttrails.util.PluginUtils
import xyz.oribuin.orilibrary.manager.DataHandler
import java.util.*
import java.util.function.Consumer

class DataManager(private val plugin: FlightTrails) : DataHandler(plugin) {

    private val cachedTrails = mutableMapOf<UUID, TrailOptions>()

    override fun enable() {
        super.enable()
        async { _ ->
            connector?.connect { it ->
                val query = "CREATE TABLE IF NOT EXISTS ${tableName}_data (" +
                        "player VARCHAR(40), " +
                        "enabled BOOLEAN, " +
                        "particle LONGTEXT, " +
                        "color VARCHAR(7), " +
                        "transitionColor VARCHAR(7), " +
                        "blockData VARCHAR(50), " +
                        "itemData VARCHAR (50), " +
                        "note INT, " +
                        "PRIMARY KEY(player))"

                it.prepareStatement(query).executeUpdate()
            }
        }

    }

    /**
     * Save all data for a player's trail options inside the Database.
     *
     * @param trail The trail options.
     */
    fun saveTrailOptions(trail: TrailOptions): TrailOptions {
        val uuid = trail.player
        cachedTrails[uuid] = trail

        async {
            connector?.connect { connection ->
                val query = "REPLACE INTO ${tableName}_data (player, enabled, particle, color, transitionColor, blockData, itemData, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, uuid.toString())
                    statement.setBoolean(2, trail.enabled)
                    statement.setString(3, trail.particle.name)
                    statement.setString(4, PluginUtils.toHex(trail.particleColor))
                    statement.setString(5, PluginUtils.toHex(trail.transitionColor))
                    statement.setString(6, trail.blockData.name)
                    statement.setString(7, trail.itemData.type.name)
                    statement.setInt(8, trail.note)
                    statement.executeUpdate()
                }

            }
        }

        return trail
    }

    /**
     * Get the flight trail options for an offline player.
     *
     * @param player  The player
     * @return
     */
    fun getTrailOptions(player: OfflinePlayer): TrailOptions? {

        if (cachedTrails[player.uniqueId] != null) {
            return cachedTrails[player.uniqueId]
        }

        var trailOptions: TrailOptions? = null
        async {
            connector?.connect { connection ->
                val query = "SELECT enabled, particle, color, transitionColor, blockData, itemData, note FROM ${tableName}_data WHERE player = ?"

                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, player.uniqueId.toString())

                    val result = statement.executeQuery()
                    if (!result.next())
                        return@use

                    val trail = TrailOptions(player.uniqueId)
                    trail.enabled = result.getBoolean("enabled")
                    trail.particle = Particle.valueOf(result.getString("particle"))
                    trail.particleColor = PluginUtils.fromHex(result.getString("color"))
                    trail.transitionColor = PluginUtils.fromHex(result.getString("transitionColor"))
                    trail.blockData = Material.valueOf(result.getString("blockData"))
                    trail.itemData = ItemStack(Material.valueOf(result.getString("itemData")))
                    trail.note = result.getInt("note")

                    trailOptions = trail
                    cachedTrails[player.uniqueId] = trail
                }
            }
        }

        return trailOptions
    }

    private fun async(callback: Consumer<BukkitTask>) {
        this.plugin.server.scheduler.runTaskAsynchronously(this.plugin, callback)
    }
}