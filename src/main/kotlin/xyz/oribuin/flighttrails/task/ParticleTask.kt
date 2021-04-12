package xyz.oribuin.flighttrails.task

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.obj.TrailOptions
import xyz.oribuin.flighttrails.util.VectorUtils
import java.util.*

class ParticleTask(private val plugin: FlightTrails) : BukkitRunnable() {

    private val data = this.plugin.getManager(DataManager::class.java)

    override fun run() {
        if (this.plugin.server.onlinePlayers.isEmpty()) return

        this.plugin.server.onlinePlayers.forEach {
            // Check if player has permission to use trails.
            if (!it.hasPermission("flighttrails.use")) return@forEach

            // Check if player is vanished
            if (it.hasMetadata("vanished")) return@forEach

            // Check if in disabled world
            if (this.plugin.config.getStringList("disabled-worlds").contains(it.world.name)) return@forEach

            // Check if gamemode is spectator
            if (it.gameMode == GameMode.SPECTATOR) return@forEach

            // Check if invisible
            if (plugin.config.getBoolean("hide-if-invisible") && it.hasPotionEffect(PotionEffectType.INVISIBILITY)) return@forEach

            // Check if can use trail in worldguard region
            if (!canUseTrailsWorldguard(it)) return@forEach

            val options = data.getTrailOptions(it, sqlOnly = false) ?: return@forEach

            // Check if the player has trails enabled
            if (!options.enabled) return@forEach

            if (it.isFlying && plugin.config.getBoolean("creative-fly-particles")) {
                this.spawnParticles(it, options, it.location)
                return@forEach
            }

            if (it.isGliding && plugin.config.getBoolean("elytra-particles")) {

                if (plugin.config.getString("elytra-particle-style").equals("legacy", ignoreCase = true)) {
                    this.spawnParticles(it, options, it.location)
                    return@forEach
                }

                val distanceFromFeetCenter = 1.20

                val leftWing = VectorUtils.rotateVector(Vector(-0.25, -0.5, distanceFromFeetCenter), it.location.yaw, it.location.pitch)
                val rightWing = VectorUtils.rotateVector(Vector(-0.25, -0.5, -distanceFromFeetCenter), it.location.yaw, it.location.pitch)


                this.spawnParticles(it, options, it.location.clone().subtract(leftWing))
                this.spawnParticles(it, options, it.location.clone().subtract(rightWing))
            }

        }

    }

    private fun spawnParticles(player: Player, trail: TrailOptions, loc: Location) {

        val particleCount = this.plugin.config.getInt("particle-amount")
        val newLoc = Location(loc.world, loc.x, loc.y - 0.1, loc.z)

        val list = plugin.config.getStringList("disabled-particles").toMutableList()
        list.addAll(listOf(Particle.LEGACY_BLOCK_CRACK.name, Particle.LEGACY_BLOCK_DUST.name, Particle.LEGACY_FALLING_DUST.name))

        if (list.contains(trail.particle.name))
            return

        when (trail.particle) {
            Particle.REDSTONE -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, Particle.DustOptions(trail.particleColor, (this.plugin.config.get("particle-size") as Int? ?: 1).toFloat()))

            Particle.SPELL_MOB, Particle.SPELL_MOB_AMBIENT -> player.world.spawnParticle(trail.particle, newLoc, 0, trail.particleColor.red / 255.0, trail.particleColor.green / 255.0, trail.particleColor.blue / 255.0, 0.0, 0.0)

            Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, 0.0, trail.blockData.createBlockData())

            Particle.ITEM_CRACK -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, 0.0, trail.itemData)

            Particle.NOTE -> player.world.spawnParticle(trail.particle, newLoc, 0, trail.note / 24.0, 0.0, 0.0, 1.0)

            else -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, 0.0)

        }


    }

    private fun canUseTrailsWorldguard(player: Player): Boolean {
        val plugin = this.plugin.server.pluginManager.getPlugin("WorldGuard") ?: return true
        this.plugin.flag ?: return true

        if (!plugin.isEnabled) return true

        val lp = WorldGuardPlugin.inst().wrapPlayer(player)
        val container = WorldGuard.getInstance().platform.regionContainer

        return container.createQuery().testState(lp.location, lp, this.plugin.flag)
    }

    init {
        val interval = if (this.plugin.config.get("spawn-interval") == null) 1 else this.plugin.config.getInt("spawn-interval")
        runTaskTimerAsynchronously(plugin, 0, interval.toLong())
    }

}