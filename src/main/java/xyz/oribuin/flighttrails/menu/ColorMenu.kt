package xyz.oribuin.flighttrails.menu

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook
import xyz.oribuin.flighttrails.library.HexUtils.colorify
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.util.TrailUtils.formatToHex
import xyz.oribuin.orilibrary.StringPlaceholders

class ColorMenu(private val plugin: FlightTrails, private val player: Player) : Listener {
    val color = plugin.getManager(DataManager::class.java).getOrSetColor(player, null)

    fun openMenu() {
        player.openInventory(menu())
    }

    private fun menu(): Inventory {
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
                inv.setItem(
                    39, normalItem(
                        Material.RED_CONCRETE, "&c-Remove &7RGB Numbers", listOf(
                            " ",
                            " &f» &bLeft Click - &7Remove 1 &cRed",
                            " &f» &bMiddle Click - &7Remove 1 &aGreen",
                            " &f» &bRight Click - &7Remove 1 &bBlue",
                            " &f» &bShift Left Click - &7Set &cRed&7 to 0",
                            " &f» &bNumber Click - &7Set &aGreen&7 to 0",
                            " &f» &bShift Right Click - &7Set &bBlue&7 to 0"
                        )
                    )
                )

                inv.setItem(40, currentColor())

                inv.setItem(
                    41, normalItem(
                        Material.GREEN_CONCRETE, "&a+Add &7RGB Numbers", listOf(
                            " ",
                            " &f» &bLeft Click - &7Add 1 &cRed",
                            " &f» &bMiddle Click - &7Add 1 &aGreen",
                            " &f» &bRight Click - &7Add 1 &bBlue",
                            " &f» &bShift Left Click - &7Set &cRed&7 to 255",
                            " &f» &bNumber Click - &7Set &aGreen&7 to 255",
                            " &f» &bShift Right Click - &7Set &bBlue&7 to 255"
                        )
                    )
                )

            }
        }, 0, 1)
        return inv
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun clickEvent(event: InventoryClickEvent) {

        if (event.whoClicked !is Player || event.view.title != "Set your color")
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

            39 -> {
                removeRGBColor(event.click)
            }

            40 -> {
                setHexColor(player)
            }

            41 -> {
                addRGBColor(event.click)
            }
        }
    }

    private fun currentColor(): ItemStack {
        val newColor = plugin.getManager(DataManager::class.java).getOrSetColor(player, null)
        val armor = ItemStack(Material.LEATHER_CHESTPLATE)
        val meta = armor.itemMeta as LeatherArmorMeta

        val clr = Color.fromRGB(newColor.red, newColor.green, newColor.blue)

        meta.setColor(clr)
        meta.setDisplayName(format("&bCurrent Colour: ${formatToHex(clr)}${newColor.red},${newColor.green},${newColor.blue}"))
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE)
        val lore = listOf(
            format("&7View your current trail color here."),
            format("&7Use side buttons to alter the RGB Code"),
            format(" "),
            format("&bClick to change trail color in #HEX")
        )

        meta.lore = lore
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

    private fun colorCommands(player: Player, color: Color) {
        val msg = plugin.getManager(MessageManager::class.java)
        val data = plugin.getManager(DataManager::class.java)

        if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle.redstone")) {
            msg.sendMessage(player, "invalid-permission")
            player.closeInventory()
            return
        }

        if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.color")) {
            msg.sendMessage(player, "invalid-permission")
            player.closeInventory()
        }

        msg.sendMessage(
            player, "set-command.color", StringPlaceholders.builder()
                .addPlaceholder("color", "${color.red}, ${color.green}, ${color.blue}")
                .addPlaceholder("hex", formatToHex(color))
                .build()
        )

        data.getOrSetColor(player, color)
        data.getOrSetParticle(player, Particle.REDSTONE)
    }

    private fun addRGBColor(clickType: ClickType): Color {
        val newColor = plugin.getManager(DataManager::class.java).getOrSetColor(player, null)

        var red = newColor.red
        var green = newColor.green
        var blue = newColor.blue

        if (clickType == ClickType.LEFT && red < 255 && red > -1) {
            red += 1
        } else if (clickType == ClickType.MIDDLE && green < 255 && green > -1) {
            green += 1
        } else if (clickType == ClickType.RIGHT && blue < 255 && blue > -1) {
            blue += 1
        } else if (clickType == ClickType.SHIFT_LEFT && red < 255) {
            red = 255
        } else if (clickType == ClickType.SHIFT_RIGHT && blue < 255) {
            blue = 255
        } else if (clickType == ClickType.NUMBER_KEY && green < 255) {
            green = 255
        }

        val data = plugin.getManager(DataManager::class.java)
        data.getOrSetColor(player, Color.fromRGB(red, green, blue))
        data.getOrSetParticle(player, Particle.REDSTONE)

        return this.color
    }

    private fun removeRGBColor(clickType: ClickType): Color {
        val newColor = plugin.getManager(DataManager::class.java).getOrSetColor(player, null)

        var red = newColor.red
        var green = newColor.green
        var blue = newColor.blue

        if (clickType == ClickType.LEFT && red > 0) {
            red -= 1
        } else if (clickType == ClickType.MIDDLE && green > 0) {
            green -= 1
        } else if (clickType == ClickType.RIGHT && blue > 0) {
            blue -= 1
        } else if (clickType == ClickType.SHIFT_LEFT) {
            red = 0
        } else if (clickType == ClickType.SHIFT_RIGHT) {
            blue = 0
        } else if (clickType == ClickType.NUMBER_KEY) {
            green = 0
        }

        val data = plugin.getManager(DataManager::class.java)
        data.getOrSetColor(player, Color.fromRGB(red, green, blue))
        data.getOrSetParticle(player, Particle.REDSTONE)
        return this.color
    }

    private fun format(string: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): String {
        return colorify(placeholders.apply(string))
    }

    private fun setHexColor(pl: Player) {
        val data = plugin.getManager(DataManager::class.java)

        AnvilGUI.Builder()
            .onComplete { _, text ->
                val msg = plugin.getManager(MessageManager::class.java)
                if (!text.startsWith("#") || text.length != 7) {
                    msg.sendMessage(pl, "invalid-hex")
                    return@onComplete AnvilGUI.Response.close()
                }

                try {

                    if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle.redstone")) {
                        msg.sendMessage(player, "invalid-permission")
                        player.closeInventory()
                        return@onComplete AnvilGUI.Response.close()
                    }

                    val clr = java.awt.Color.decode(text)
                    data.getOrSetColor(player, Color.fromRGB(clr.red, clr.green, clr.blue))
                    data.getOrSetParticle(player, Particle.REDSTONE)

                    msg.sendMessage(
                        player, "set-command.color", StringPlaceholders.builder()
                            .addPlaceholder("color", "${clr.red}, ${clr.green}, ${clr.blue}")
                            .addPlaceholder("hex", formatToHex(color))
                            .build()
                    )

                    return@onComplete AnvilGUI.Response.close()

                } catch (ex: NumberFormatException) {
                    msg.sendMessage(pl, "invalid-hex")
                    return@onComplete AnvilGUI.Response.close()
                }
            }
            .plugin(plugin)
            .title("Set trail hex color")
            .text(formatToHex(data.getOrSetColor(pl, null)))
            .open(pl)
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }
}