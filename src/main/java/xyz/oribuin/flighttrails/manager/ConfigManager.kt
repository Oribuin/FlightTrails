package xyz.oribuin.flighttrails.manager

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.orilibrary.manager.Manager
import java.io.File

class ConfigManager(plugin: FlightTrails) : Manager(plugin) {
    override fun enable() {
        plugin.reloadConfig()
        plugin.saveDefaultConfig()
        val config = plugin.config

        for (value in Setting.values()) {
            if (config.get(value.key) == null) {
                config.set(value.key, value.defaultValue)
            }
            value.load(config)
        }

        config.save(File(plugin.dataFolder, "config.yml"))
    }

    enum class Setting(val key: String, val defaultValue: Any) {
        CREATIVE_FLY_TRAILS("conditions.creative-fly", true),
        ELYTRA_TRAILS("conditions.elytra", true),
        DISABLED_WORLDS("disabled-worlds", listOf("disabled-world1", "disabled-world2")),
        DISABLED_PARTICLES("disabled-particles", listOf("EXPLOSION_LARGE", "EXPLOSION_HUGE")),
        PS_COUNT("particle-settings.count", 2),
        PS_SIZE("particle-settings.size", 1.0.toFloat()),
        PS_ELYTRA_STYLE("particle-settings.elytra-style", "FANCY"),
        PS_DEFAULT_ENABLED("particle-settings.default.enabled", true),
        PS_DEFAULT_PARTICLE("particle-settings.default.particle", Particle.FLAME.name),
        PS_DEFAULT_BLOCK("particle-settings.default.block", Material.LIGHT_BLUE_WOOL.name),
        PS_DEFAULT_ITEM("particle-settings.default.item", ItemStack(Material.LIGHT_BLUE_WOOL)),
        PS_DEFAULT_COLOR("particle-settings.default.color", Color.AQUA);

        private var value: Any? = null

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        val boolean: Boolean
            get() = value as Boolean

        /**
         * @return the setting as an int
         */
        val int: Int
            get() = number.toInt()

        /**
         * @return the setting as a long
         */
        val long: Long
            get() = number.toLong()

        /**
         * @return the setting as a double
         */
        val double: Double
            get() = number

        /**
         * @return the setting as a float
         */
        val float: Float
            get() = number.toFloat()

        /**
         * @return the setting as a String
         */
        val string: String
            get() = value as String

        private val number: Double
            get() {
                if (value is Int) {
                    return (value as Int).toDouble()
                } else if (value is Short) {
                    return (value as Short).toDouble()
                } else if (value is Byte) {
                    return (value as Byte).toDouble()
                } else if (value is Float) {
                    return (value as Float).toDouble()
                }
                return value as Double
            }

        /**
         * @return the setting as a string list
         */
        val stringList: List<*>
            get() = value as List<*>

        /**
         * Loads the value from the config and caches it
         */
        fun load(config: FileConfiguration) {
            value = config[key]
        }

    }

    override fun disable() {
        // Unused
    }
}