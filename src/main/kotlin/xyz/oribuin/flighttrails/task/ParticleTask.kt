package xyz.oribuin.flighttrails.task

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
            if (!it.hasPermission("flighttrails.use")) return@forEach
            if (it.hasMetadata("vanished")) return@forEach
            if (!this.plugin.toggleList.contains(it.uniqueId)) return@forEach
            if (this.plugin.config.getStringList("disabled-worlds").contains(it.world.name)) return@forEach
            if (it.gameMode == GameMode.SPECTATOR) return@forEach
            if (plugin.config.getBoolean("hide-if-invisible") && it.hasPotionEffect(PotionEffectType.INVISIBILITY)) return@forEach

            val options = data.getTrailOptions(it, sqlOnly = false) ?: return@forEach

            if (it.isFlying && plugin.config.getBoolean("creative-fly-particles")) this.spawnParticles(it, options, it.location)
            else if (it.isGliding && plugin.config.getBoolean("elytra-particles")) {

                if (plugin.config.getString("elytra-particle-style").equals("legacy", ignoreCase = true)) {
                    this.spawnParticles(it, options, it.location)
                    return@forEach
                }

                val distanceFromFeetCenter = 0.75

                val leftWing = VectorUtils.rotateVector(Vector(-0.25, -0.4, distanceFromFeetCenter), it.location.yaw, it.location.pitch)
                val rightWing = VectorUtils.rotateVector(Vector(-0.25, -0.4, -distanceFromFeetCenter), it.location.yaw, it.location.pitch)


                this.spawnParticles(it, options, it.location.clone().subtract(leftWing))
                this.spawnParticles(it, options, it.location.clone().subtract(rightWing))
            }
        }

    }

    private fun spawnParticles(player: Player, trail: TrailOptions, loc: Location) {

        val particleCount = this.plugin.config.getInt("particle-amount")
        val newLoc = Location(loc.world, loc.x, loc.y - 0.1, loc.z)

        when (trail.particle) {
            Particle.REDSTONE -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, Particle.DustOptions(trail.particleColor, (this.plugin.config.get("particle-size") as Int? ?: 1).toFloat()))

            Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, trail.blockData.createBlockData())

            Particle.ITEM_CRACK -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, trail.itemData)

            Particle.NOTE -> player.world.spawnParticle(trail.particle, newLoc, 0, 0.0, 0.0, 0.0, trail.note / 24.0)

            else -> player.world.spawnParticle(trail.particle, newLoc, particleCount, 0.0, 0.0, 0.0, 0.0)

        }


    }

    init {
        runTaskTimerAsynchronously(plugin, 0, 1)
    }

}