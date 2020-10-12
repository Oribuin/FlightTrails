package xyz.oribuin.flighttrails.menu

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook
import xyz.oribuin.flighttrails.library.HexUtils.colorify
import xyz.oribuin.flighttrails.library.StringPlaceholders
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager

class MainMenu(private val plugin: FlightTrails, private val player: Player) : Listener {

    fun openMenu() {
        player.openInventory(menu())
    }

    private fun menu(): Inventory {
        val inv = Bukkit.createInventory(null, 45, format("FlightTrails Menu"))

        for (i in 0..44) {
            val item = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
            val meta = item.itemMeta ?: return inv
            meta.setDisplayName(" ")
            item.itemMeta = meta
            inv.setItem(i, item)
        }

        inv.setItem(10, normalItem(Material.RED_DYE, "&bColor Menu", listOf(
                "&7Click to access the color menu",
                "&7to change your redstone particle color",
                " ",
                "&b&lRequires redstone Particle"
        )))

        return inv
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun clickEvent(event: InventoryClickEvent) {

        if (event.whoClicked !is Player || event.view.title != "FlightTrails Menu")
            return

        val data = plugin.getManager(DataManager::class)
        val msg = plugin.getManager(MessageManager::class)

        val player = event.whoClicked as Player
        event.cursor ?: return
        event.isCancelled = true

        when (event.slot) {
            10 -> {

                if (!player.hasPermission("flighttrails.admin") && data.setParticle(player, null) != Particle.REDSTONE) {
                    msg.sendMessage(player, "set-command.required-particle")
                    player.closeInventory()
                    return
                }

                ColorMenu(plugin, player).openMenu()
            }
        }
    }


    private fun normalItem(material: Material, name: String, lore: List<String>): ItemStack {
        val itemStack = ItemStack(material)
        val meta = itemStack.itemMeta ?: return ItemStack(Material.AIR)
        meta.setDisplayName(PlaceholderAPIHook.apply(player, colorify(name)))
        val coloredLore = mutableListOf<String>()
        lore.forEach { s -> coloredLore.add(PlaceholderAPIHook.apply(player, colorify(s))) }
        meta.lore = coloredLore
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        itemStack.itemMeta = meta
        return itemStack
    }

    private fun format(string: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): String {
        return colorify(placeholders.apply(string))
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }
}