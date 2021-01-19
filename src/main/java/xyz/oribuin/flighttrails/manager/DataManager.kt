package xyz.oribuin.flighttrails.manager

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.orilibrary.util.FileUtils.createFile
import xyz.oribuin.orilibrary.manager.Manager
import java.io.File

/**
 * @author Oribuin
 */
class DataManager(plugin: FlightTrails) : Manager(plugin) {
    private var dataConfig: FileConfiguration? = null

    // Reload Method
    override fun enable() {
        createFile(plugin, "data.yml")
        dataConfig = YamlConfiguration.loadConfiguration(dataFile)
    }

    // Disable Method
    override fun disable() {
        // Unused
    }

    /**
     * Get or set the trail particle for a player
     *
     * @param player The player being set.
     * @param particle The particle being set.
     */
    fun getOrSetParticle(player: Player, particle: Particle?): Particle {
        if (particle != null) {
            getConfig().set(player.uniqueId.toString() + ".particle", particle.name)
            saveData()
            return particle
        }

        return Particle.valueOf(getConfig().getString(player.uniqueId.toString() + ".particle")?.toUpperCase() ?: return Particle.valueOf(plugin.config.getString("particle-settings.default.particle") ?: return Particle.FLAME))
    }

    /**
     * Get or set the trail block for a player
     *
     * @param player The player being set.
     * @param material The material being set.
     */
    fun getOrSetBlock(player: Player, material: Material?): Material {
        if (material != null) {
            getConfig().set(player.uniqueId.toString() + ".block", material.name)
            saveData()
            return material
        }

        return Material.valueOf(getConfig().getString(player.uniqueId.toString() + ".block")?.toUpperCase() ?: return Material.valueOf(plugin.config.getString("particle-settings.default.block") ?: return Material.LIGHT_BLUE_WOOL))
    }

    /**
     * Get or set the trail Item for a player
     *
     * @param player The player being set.
     * @param itemStack The item being set.
     */
    fun getOrSetItem(player: Player, itemStack: ItemStack?): ItemStack {
        if (itemStack != null) {
            getConfig().set(player.uniqueId.toString() + ".item", itemStack)
            saveData()
            return itemStack
        }

        return getConfig().getItemStack(player.uniqueId.toString() + ".item") ?: return plugin.config.getItemStack("particle-settings.default.item") ?: return ItemStack(Material.LIGHT_BLUE_WOOL)
    }

    /**
     * Get or set the trail color for a player
     *
     * @param player The player being set.
     * @param color The color
     * being set.
     */
    fun getOrSetColor(player: Player, color: Color?): Color {
        if (color != null) {
            getConfig().set(player.uniqueId.toString() + ".color", color)
            saveData()
            return color
        }

        return getConfig().getColor(player.uniqueId.toString() + ".color") ?: return plugin.config.getColor("particle-settings.default.color") ?: return Color.AQUA
    }

    /**
     * Get or set if the player has trails
     *
     * @param player The player being set
     * @param enabled if they are enabled or not
     */
    fun getOrSetEnabled(player: Player, enabled: Boolean?): Boolean {
        if (enabled != null) {
            getConfig().set(player.uniqueId.toString() + ".enabled", enabled)
            saveData()
            return enabled
        }

        return getConfig().getBoolean(player.uniqueId.toString() + ".enabled")
    }

    /**
     * Set the default values for a player
     */
    fun setDefault(player: Player) {
        val uuid = player.uniqueId.toString()
        getConfig().set("$uuid.enabled", plugin.config.getBoolean("particle-settings.default.enabled"))
        getConfig().set("$uuid.block", getOrSetBlock(player, null).name)
        getConfig().set("$uuid.color", getOrSetColor(player, null))
        getConfig().set("$uuid.particle", getOrSetParticle(player, null).name)
        getConfig().set("$uuid.item", getOrSetItem(player, null))
        saveData()
    }

    /**
     * Get the player from the config
     *
     * @param player The player being obtained
     */
    fun getPlayer(player: Player): Player? {
        return getConfig().getOfflinePlayer(player.uniqueId.toString())?.player
    }

    /**
     * Get the configuration as not nullable
     *
     * @return dataConfig
     */
    fun getConfig(): FileConfiguration {
        return dataConfig ?: return plugin.config
    }

    /**
     * Save the trail data
     */
    private fun saveData() {
        dataConfig?.save(dataFile)
    }

    /**
     * Get the Data File from plugin folder
     */
    private val dataFile: File get() = File(plugin.dataFolder, "data.yml")
}