package xyz.oribuin.flighttrails.manager

import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.obj.TrailOptions
import xyz.oribuin.flighttrails.util.PluginUtils
import xyz.oribuin.orilibrary.database.DatabaseConnector
import xyz.oribuin.orilibrary.database.MySQLConnector
import xyz.oribuin.orilibrary.database.SQLiteConnector
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.Nullable
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.FileUtils
import java.sql.Connection
import java.util.*
import java.util.function.Consumer

class DataManager(private val plugin: FlightTrails) : Manager(plugin) {

    private var connector: DatabaseConnector? = null
    private val cachedTrails: MutableMap<UUID, TrailOptions> = HashMap()

    override fun enable() {
        val config = this.plugin.config
        if (config.getBoolean("mysql.enabled")) {

            // Define SQL Values
            val hostName = config.getString("mysql.host") ?: return
            val port = config.getInt("mysql.port")
            val dbname = config.getString("mysql.dbname") ?: return
            val username = config.getString("mysql.username") ?: return
            val password = config.getString("mysql.password") ?: return
            val ssl = config.getBoolean("mysql.ssl")

            // Connect to MySQL
            connector = MySQLConnector(this.plugin, hostName, port, dbname, username, password, ssl)
            this.plugin.logger.info("Connected to MySQL for data saving ~ $hostName:$port")
        } else {
            // Create DB Files
            FileUtils.createFile(plugin, "FlightTrails.db")

            // Connect to SQLite
            connector = SQLiteConnector(this.plugin, "FlightTrails.db")
            this.plugin.logger.info("Connected to SQLite for data saving ~ /plugins/FlightTrails/FlightTrails.db")
        }
        createTables()
    }

    /**
     * Create all the required SQL Tables for the plugin.
     */
    private fun createTables() {
        val queries = arrayOf(
            "CREATE TABLE IF NOT EXISTS flighttrails_data (player VARCHAR(40), particle LONGTEXT, color VARCHAR(7), blockData VARCHAR(50), itemData VARCHAR(50), note INT, PRIMARY KEY(player))"
        )

        async {
            connector?.connect { connection: Connection ->
                connection.createStatement().use { statement ->
                    for (query in queries) statement.addBatch(query)
                    statement.executeBatch()
                }
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
                val query = "REPLACE INTO flighttrails_data (player, particle, color, blockData, itemData, note) VALUES (?, ?, ?, ?, ?, ?)"

                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, uuid.toString())
                    statement.setString(2, trail.particle.name)
                    statement.setString(3, PluginUtils.toHex(trail.particleColor))
                    statement.setString(4, trail.blockData.name)
                    statement.setString(5, trail.itemData.type.name)
                    statement.setInt(6, trail.note)
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
     * @param sqlOnly true if you only want to get trail options from sql
     * @return
     */
    fun getTrailOptions(player: OfflinePlayer, sqlOnly: Boolean = true): @Nullable TrailOptions? {

        if (!sqlOnly && cachedTrails[player.uniqueId] != null) {
            return cachedTrails[player.uniqueId]
        }

        var trailOptions: TrailOptions? = null
        Thread() {
            connector?.connect { connection ->
                val query = "SELECT * FROM flighttrails_data WHERE player = ?"

                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, player.uniqueId.toString())

                    val result = statement.executeQuery()
                    if (!result.next()) return@use

                    val trail = TrailOptions(player.uniqueId)
                    trail.particle = Particle.valueOf(result.getString("particle"))
                    trail.particleColor = PluginUtils.fromHex(result.getString("color"))
                    trail.blockData = Material.valueOf(result.getString("blockData"))
                    trail.itemData = ItemStack(Material.valueOf(result.getString("itemData")))
                    trail.note = result.getInt("note")

                    trailOptions = trail
                    cachedTrails[player.uniqueId] = trail

                }
            }
        }.start()

        // Make sure the player has their trail options saved
        if (trailOptions == null) return this.saveTrailOptions(TrailOptions(player.uniqueId))

        return trailOptions
    }


    override fun disable() {
        this.connector?.closeConnection()
    }

    private fun async(callback: Consumer<BukkitTask>) {
        Thread { this.plugin.server.scheduler.runTaskAsynchronously(this.plugin, callback) }.start()
    }
}