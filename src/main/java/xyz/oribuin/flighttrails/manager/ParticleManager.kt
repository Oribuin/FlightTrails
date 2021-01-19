package xyz.oribuin.flighttrails.manager

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.util.VectorUtils
import xyz.oribuin.orilibrary.manager.Manager

/**
 * @author Oribuin
 */
class ParticleManager(plugin: FlightTrails) : Manager(plugin) {

    // Reload Method
    override fun enable() {
        Bukkit.getScheduler().cancelTasks(plugin)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable { this.spawnParticles() }, 0, 0.1.toLong())
    }

    /**
     * General checks for spawning particles
     */
    private fun spawnParticles() {
        Bukkit.getOnlinePlayers().forEach { player ->

            val data = plugin.getManager(DataManager::class.java)


            // Check for perm, if vanished, has trails enabled, is in an enabled world and not in spectator
            if (!player.hasPermission("flighttrails.use")
                || player.hasMetadata("vanished")
                || !data.getOrSetEnabled(player, null)
                || plugin.config.getStringList("disabled-worlds").contains(player.name)
                || player.gameMode == GameMode.SPECTATOR
            )
                return@forEach


            // If creative fly trails are enabled and the player is flying
            if (ConfigManager.Setting.CREATIVE_FLY_TRAILS.boolean && player.isFlying) {
                this.spawnFeetParticle(player)
                return@forEach
            } else if (ConfigManager.Setting.ELYTRA_TRAILS.boolean && player.isGliding) {
                if (ConfigManager.Setting.PS_ELYTRA_STYLE.string.equals("LEGACY", true)) {
                    this.spawnFeetParticle(player)
                } else {
                    spawnElytraParticles(player)
                }
                return@forEach
            }
        }
    }

    /**
     * Spawn particles at the feet
     *
     * @param player The player that the particles are being spawned at
     */
    private fun spawnFeetParticle(particleOwner: Player) {
        val data = plugin.getManager(DataManager::class.java)
        val particleCount = ConfigManager.Setting.PS_COUNT.int
        val color = data.getOrSetColor(particleOwner, null)
        val particle = data.getOrSetParticle(particleOwner, null)

        if (plugin.config.getStringList("disabled-particles").contains(particle.name))
            return

        when (particle) {
            Particle.REDSTONE -> {
                val dustOptions = DustOptions(Color.fromRGB(color.red, color.green, color.blue), ConfigManager.Setting.PS_SIZE.float)
                particleOwner.world.spawnParticle(particle, particleOwner.location.subtract(0.0, 0.1, 0.0), particleCount, 0.0, 0.0, 0.0, dustOptions)
            }

            Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> {
                particleOwner.world.spawnParticle(particle, particleOwner.location.subtract(0.0, 0.1, 0.0), particleCount, 0.0, 0.0, 0.0, data.getOrSetBlock(particleOwner, null).createBlockData())
            }

            Particle.ITEM_CRACK -> {
                particleOwner.world.spawnParticle(particle, particleOwner.location.subtract(0.0, 0.1, 0.0), particleCount, 0.0, 0.0, 0.0, data.getOrSetItem(particleOwner, null))
            }

            else -> {
                particleOwner.world.spawnParticle(particle, particleOwner.location.subtract(0.0, 0.1, 0.0), particleCount, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }

    /**
     * Spawn particles at the feet or wings
     *
     * @param player The player that the particles are being spawned at
     */
    private fun spawnElytraParticles(player: Player) {
        val data = plugin.getManager(DataManager::class.java)
        val color = data.getOrSetColor(player, null)
        val particle = data.getOrSetParticle(player, null)

        val particleCount = ConfigManager.Setting.PS_COUNT.int
        val loc = player.location
        val distanceFromFeetCenter = 0.75

        val leftWing = VectorUtils.rotateVector(Vector(-0.25, -0.4, distanceFromFeetCenter), loc.yaw, loc.pitch)
        val rightWing = VectorUtils.rotateVector(Vector(-0.25, -0.4, -distanceFromFeetCenter), loc.yaw, loc.pitch)

        if (plugin.config.getStringList("disabled-particles").contains(particle.name))
            return


        when (particle) {
            Particle.REDSTONE -> {
                val dustOptions = DustOptions(Color.fromRGB(color.red, color.green, color.blue), ConfigManager.Setting.PS_SIZE.float)
                player.world.spawnParticle(particle, loc.clone().subtract(leftWing), particleCount, 0.0, 0.0, 0.0, dustOptions)
                player.world.spawnParticle(particle, loc.clone().subtract(rightWing), particleCount, 0.0, 0.0, 0.0, dustOptions)
            }

            Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> {
                player.world.spawnParticle(particle, loc.clone().subtract(leftWing), particleCount, 0.0, 0.0, 0.0, data.getOrSetBlock(player, null).createBlockData())
                player.world.spawnParticle(particle, loc.clone().subtract(rightWing), particleCount, 0.0, 0.0, 0.0, data.getOrSetBlock(player, null).createBlockData())
            }

            Particle.ITEM_CRACK -> {
                player.world.spawnParticle(particle, loc.clone().subtract(leftWing), particleCount, 0.0, 0.0, 0.0, data.getOrSetItem(player, null))
                player.world.spawnParticle(particle, loc.clone().subtract(rightWing), particleCount, 0.0, 0.0, 0.0, data.getOrSetItem(player, null))
            }

            else -> {
                player.world.spawnParticle(particle, loc.clone().subtract(leftWing), particleCount, 0.0, 0.0, 0.0, 0.0)
                player.world.spawnParticle(particle, loc.clone().subtract(rightWing), particleCount, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }


    // Disable Method
    override fun disable() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }
}