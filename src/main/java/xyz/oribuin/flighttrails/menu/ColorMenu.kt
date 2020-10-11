package xyz.oribuin.flighttrails.menu

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.library.HexUtils.colorify
import xyz.oribuin.flighttrails.library.PluginUtils.formatToHex
import xyz.oribuin.flighttrails.library.StringPlaceholders
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager

class ColorMenu(private val plugin: FlightTrails, private val player: Player) : Listener {

    fun openMenu() {
        player.openInventory(menu())
    }

    private fun menu(): Inventory {
        val craftPlayer = player as CraftPlayer

        val data = plugin.getManager(DataManager::class)
        val color = data.setColor(player, null)

        val inv = Bukkit.createInventory(null, 54, format("Set your color"))

        for (i in 0..53) {
            val item = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
            val meta = item.itemMeta ?: return inv
            meta.setDisplayName(" ")
            item.itemMeta = meta
            inv.setItem(i, item)
        }

        inv.setItem(1, colorItem(Material.RED_DYE, "&7Trail color &cRed.", Color.RED))
        inv.setItem(2, colorItem(Material.ORANGE_DYE, "&7Trail color &6Orange", Color.ORANGE))
        inv.setItem(3, colorItem(Material.YELLOW_DYE, "&7Trail color &eYellow", Color.YELLOW))
        inv.setItem(4, colorItem(Material.LIME_DYE, "&7Trail color &aLime", Color.LIME))
        inv.setItem(5, colorItem(Material.GREEN_DYE, "&7Trail color &2Green", Color.GREEN))
        inv.setItem(6, colorItem(Material.LIGHT_BLUE_DYE, "&7Trail color &bAqua", Color.AQUA))
        inv.setItem(7, colorItem(Material.CYAN_DYE, "&7Trail color &3Cyan", Color.TEAL))
        inv.setItem(10, colorItem(Material.BLUE_DYE, "&7Trail color &9Blue.", Color.BLUE))
        inv.setItem(11, colorItem(Material.PURPLE_DYE, "&7Trail color &5Purple", Color.PURPLE))
        inv.setItem(12, colorItem(Material.MAGENTA_DYE, "&7Trail color #FF00FFMagenta", Color.FUCHSIA))
        inv.setItem(13, colorItem(Material.PINK_DYE, "&7Trail color &dPink", Color.fromRGB(255, 182, 193)))
        inv.setItem(14, colorItem(Material.WHITE_DYE, "&7Trail color &fWhite", Color.WHITE))
        inv.setItem(15, colorItem(Material.LIGHT_GRAY_DYE, "&7Trail color &lLight Gray", Color.SILVER))
        inv.setItem(16, colorItem(Material.GRAY_DYE, "&7Trail color &8Gray", Color.GRAY))
        inv.setItem(22, colorItem(Material.BLACK_DYE, "&7Trail color &8&lBlack", Color.BLACK))

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            if (player.hasPermission("flighttrails.color.custom")) {
                inv.setItem(40, currentColor(color))
            }
        }, 0, 1)
        return inv
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun clickEvent(event: InventoryClickEvent) {
        val topInventory = event.view.topInventory
        val inventory = event.clickedInventory

        if (inventory == null || event.whoClicked !is Player || inventory != topInventory || event.view.title != "Set your color")
            return

        val player = event.whoClicked as Player
        event.cursor ?: return
        event.isCancelled = true

        when (event.slot) {
            1 -> {
                this.colorCommands(player, Color.RED)
            }

            2 -> {
                colorCommands(player, Color.ORANGE)
            }

            3 -> {
                colorCommands(player, Color.YELLOW)
            }

            4 -> {
                colorCommands(player, Color.LIME)
            }

            5 -> {
                colorCommands(player, Color.GREEN)
            }

            6 -> {
                colorCommands(player, Color.AQUA)
            }

            7 -> {
                colorCommands(player, Color.TEAL)
            }

            10 -> {
                colorCommands(player, Color.BLUE)
            }

            11 -> {
                colorCommands(player, Color.PURPLE)
            }

            12 -> {
                colorCommands(player, Color.FUCHSIA)
            }

            13 -> {
                colorCommands(player, Color.fromRGB(255, 182, 193))
            }

            14 -> {
                colorCommands(player, Color.WHITE)
            }

            15 -> {
                colorCommands(player, Color.SILVER)
            }

            16 -> {
                colorCommands(player, Color.GRAY)
            }

            22 -> {
                colorCommands(player, Color.BLACK)
            }
        }
    }

    private fun currentColor(color: Color): ItemStack {
        val armor = ItemStack(Material.LEATHER_CHESTPLATE)
        val meta = armor.itemMeta as LeatherArmorMeta

        meta.setColor(Color.fromRGB(color.red, color.green, color.blue))
        meta.setDisplayName(format("&bCurrent Colour:${formatToHex(color)} ${color.red},${color.green},${color.blue}"))

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)

        armor.itemMeta = meta
        return armor
    }

    private fun colorItem(material: Material, name: String, color: Color?): ItemStack {
        val itemStack = ItemStack(material)
        val meta = itemStack.itemMeta ?: return ItemStack(Material.AIR)

        val placeholders = StringPlaceholders.builder()
                .addPlaceholder("hex", formatToHex(color))
                .addPlaceholder("red", color?.red)
                .addPlaceholder("green", color?.green)
                .addPlaceholder("blue", color?.blue)
                .build()

        meta.setDisplayName(format(name, placeholders))

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)

        itemStack.itemMeta = meta
        return itemStack
    }

    private fun colorCommands(player: Player, color: Color) {
        val data = plugin.getManager(DataManager::class)
        val msg = plugin.getManager(MessageManager::class)

        if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle.redstone")) {
            msg.sendMessage(player, "invalid-permission")
            player.closeInventory()
            return
        }

        if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.color")) {
            msg.sendMessage(player, "invalid-permission")
            player.closeInventory()
        }

        data.setColor(player, color)
        data.setParticle(player, Particle.REDSTONE)
        player.closeInventory()
        msg.sendMessage(player, "set-command.color", StringPlaceholders.builder()
                .addPlaceholder("color", "${color.red}, ${color.green}, ${color.blue}")
                .addPlaceholder("hex", formatToHex(color))
                .build())
    }

    private fun format(string: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): String {
        return colorify(placeholders.apply(string))
    }


    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }
}