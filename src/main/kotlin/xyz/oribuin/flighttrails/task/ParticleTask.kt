package xyz.oribuin.flighttrails.task

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitRunnable
import xyz.oribuin.flighttrails.FlightTrails

class ParticleTask(private val plugin: FlightTrails) : BukkitRunnable() {
    private var currentNumber = 0

    override fun run() {
        val loc = plugin.particleLoc ?: return

        currentNumber++
        if (currentNumber == 25) currentNumber = 1

        Bukkit.getOnlinePlayers().forEach { it.spawnParticle(Particle.NOTE, loc, 0, currentNumber / 24.0, 0.0, 0.0, 1.0) }
    }

    init {
        runTaskTimerAsynchronously(plugin, 0, 40)
    }

}