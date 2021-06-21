package xyz.oribuin.flighttrails.obj

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import java.util.*

class TrailOptions(val player: UUID) {

    var enabled = false
    var particle = Particle.FLAME
    var particleColor: Color = Color.BLACK
    var transitionColor: Color = Color.WHITE
    var blockData = Material.BLACK_WOOL
    var itemData = ItemStack(Material.BLACK_WOOL)
    var note = 0

}