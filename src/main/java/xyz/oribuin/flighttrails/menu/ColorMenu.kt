package xyz.oribuin.flighttrails.menu

import dev.rosewood.guiframework.GuiFactory
import dev.rosewood.guiframework.GuiFramework
import dev.rosewood.guiframework.framework.util.GuiUtil
import dev.rosewood.guiframework.gui.ClickAction
import dev.rosewood.guiframework.gui.GuiSize
import dev.rosewood.guiframework.gui.GuiString
import dev.rosewood.guiframework.gui.screen.GuiScreen
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.LeatherArmorMeta
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.hook.PAPIHook
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.util.HexUtils.colorify
import xyz.oribuin.flighttrails.util.ItemBuilder
import xyz.oribuin.flighttrails.util.TrailUtils.formatToHex
import xyz.oribuin.orilibrary.util.StringPlaceholders

class ColorMenu(private val plugin: FlightTrails, private val player: Player) : Listener {

    private var cachedColor = plugin.getManager(DataManager::class.java).getOrSetColor(player, null)

    private val container = GuiFactory.createContainer()
        .setTickRate(5)

    private val framework = GuiFramework.instantiate(plugin)

    private fun buildGui() {
        this.container.addScreen(mainMenu())
        this.framework.guiManager.registerGui(container)
    }

    private fun mainMenu(): GuiScreen {
        val screen = GuiFactory.createScreen(container, GuiSize.ROWS_SIX)
            .setTitle("FlightTrails | Set Trail Color")

        val fillerItem = ItemBuilder()
            .setMaterial(Material.GRAY_STAINED_GLASS_PANE)
            .setName(" ")
            .build()

        GuiUtil.fillScreen(screen, fillerItem)

        val cacheColors = StringPlaceholders.builder()
            .addPlaceholder("hex", formatToHex(cachedColor))
            .addPlaceholder("red", cachedColor.red)
            .addPlaceholder("green", cachedColor.green)
            .addPlaceholder("blue", cachedColor.blue)
            .build()


        // Set main colours
        ColorValue.values().forEach {

            screen.addButtonAt(
                it.slot, GuiFactory.createButton()
                    .setName(colorify("#268cc7&lSet Trail to ${it.colorName}"))
                    .setLoreSupplier { listOf(fs("&fClick to set your dust"), fs("&ftrail color to this.")) }
                    .setIcon(it.material)
                    .setGlowing(this.cachedColor == it.color)
                    .setClickAction({ _ -> this.colorCommands(player, it); ClickAction.NOTHING })
            )
        }

        // Set RED
        screen.addButtonAt(37, GuiFactory.createButton()
            .setIcon(Material.RED_STAINED_GLASS_PANE)
            .setName(colorify("&c&lChange Red"))
            .setLoreSupplier { listOf(fs("&fClick to change the red value."), fs("&fSet number between 0-255"), fs(), fs("&fRed is currently &c${cachedColor.red}")) }
            .setClickAction({ this.setColorRGB(RGBType.RED); ClickAction.NOTHING })
        )

        // Set Green
        screen.addButtonAt(38, GuiFactory.createButton()
            .setIcon(Material.LIME_STAINED_GLASS_PANE)
            .setName(colorify("&a&lChange Green"))
            .setLoreSupplier { listOf(fs("&fClick to change the green value."), fs("&fSet number between 0-255"), fs(), fs("&fGreen is currently &a${cachedColor.green}")) }
            .setClickAction({ this.setColorRGB(RGBType.GREEN); ClickAction.NOTHING })
        )

        // Set Blue
        screen.addButtonAt(39, GuiFactory.createButton()
            .setIcon(Material.BLUE_STAINED_GLASS_PANE)
            .setName(colorify("&b&lChange Blue"))
            .setLoreSupplier { listOf(fs("&fClick to change the blue value."), fs("&fSet number between 0-255"), fs(), fs("&fBlue is currently &b${cachedColor.blue}")) }
            .setClickAction({ this.setColorRGB(RGBType.BLUE); ClickAction.NOTHING })
        )

        screen.addButtonAt(41, GuiFactory.createButton()
            .setIcon(Material.PAPER)
            .setName(colorify("&b&lSet Hex Code"))
            .setLoreSupplier { listOf(fs("&fClick to change the color"), fs("&fThrough a hex value.")) }
            .setClickAction({ this.setHexColor(player); ClickAction.NOTHING })
        )

        // Confirm Change to improve performance
        screen.addButtonAt(43, GuiFactory.createButton()
            .setIconSupplier {
                GuiFactory.createIcon(Material.LEATHER_CHESTPLATE) {
                    it as LeatherArmorMeta
                    it.setColor(cachedColor)
                    it.addItemFlags(*ItemFlag.values())
                }
            }
            .setName(colorify("&b&lConfirm Trail Color Change"))
            .setLoreSupplier {
                val placeholders = StringPlaceholders.builder()
                    .addPlaceholder("hex", formatToHex(cachedColor))
                    .addPlaceholder("red", cachedColor.red)
                    .addPlaceholder("green", cachedColor.green)
                    .addPlaceholder("blue", cachedColor.blue)
                    .build()


                listOf(fs("&fClick to confirm the change"), fs("&fof your trail color"), fs(), fs("&fCurrent color is &c%red%&f, &a%green%&f, &b%blue%", placeholders), fs("&cThis will change your particle to Dust.")) }
            .setClickAction({
                val data = plugin.getManager(DataManager::class.java)

                data.getOrSetParticle(player, Particle.REDSTONE)
                data.getOrSetColor(player, cachedColor)

                plugin.getManager(MessageManager::class.java).sendMessage(player, "set-command.color", cacheColors)
                ClickAction.CLOSE
            })
        )
        return screen
    }

    // fun formatString(): GuiString
    private fun fs(string: String = " ", placeholders: StringPlaceholders = StringPlaceholders.empty()): GuiString {
        return GuiFactory.createString(PAPIHook.apply(player, placeholders.apply(colorify(string))))
    }

    private fun colorCommands(player: Player, colorValue: ColorValue) {
        val msg = plugin.getManager(MessageManager::class.java)

        if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle.redstone")) {
            msg.sendMessage(player, "invalid-permission")
            player.closeInventory()
            return
        }

        if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.color.${colorValue.name.toLowerCase()}")) {
            msg.sendMessage(player, "invalid-permission")
            player.closeInventory()
            return
        }

        cachedColor = colorValue.color
    }

    private fun setColorRGB(type: RGBType) {
        val msgs = plugin.getManager(MessageManager::class.java)

        AnvilGUI.Builder()
            .onComplete { _, text ->
                try {
                    val number = text.toInt()

                    if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle.redstone") || !player.hasPermission("flighttrails.color.custom")) {
                        msgs.sendMessage(player, "invalid-permission")
                        player.closeInventory()
                        return@onComplete AnvilGUI.Response.close()
                    }

                    if (number > 255 || number < 0) {
                        msgs.sendMessage(player, "invalid-rgb")
                        return@onComplete AnvilGUI.Response.close()
                    }

                    when (type) {
                        RGBType.RED -> {
                            cachedColor.red = number
                        }

                        RGBType.GREEN -> {
                            cachedColor.green = number
                        }

                        RGBType.BLUE -> {
                            cachedColor.blue = number
                        }
                    }
                } catch (ex: NumberFormatException) {
                    msgs.sendMessage(player, "invalid-rgb")
                }
                AnvilGUI.Response.close()
            }
            .title("Set RGB Color")
            .text(if (type == RGBType.RED) cachedColor.red.toString() else if (type == RGBType.GREEN) cachedColor.green.toString() else cachedColor.blue.toString())
            .plugin(plugin)
            .onClose { this.openMenu() }
            .open(player)
    }

    private fun setHexColor(pl: Player) {
        val data = plugin.getManager(DataManager::class.java)

        AnvilGUI.Builder()
            .onComplete { _, text ->

                val msg = plugin.getManager(MessageManager::class.java)
                try {
                    if (!text.startsWith("#") || text.length != 7) {
                        msg.sendMessage(pl, "invalid-hex")
                        return@onComplete AnvilGUI.Response.close()
                    }


                    if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle.redstone") || !player.hasPermission("flighttrails.color.custom")) {
                        msg.sendMessage(player, "invalid-permission")
                        player.closeInventory()
                        return@onComplete AnvilGUI.Response.close()
                    }

                    val clr = java.awt.Color.decode(text)
                    cachedColor = Color.fromRGB(clr.red, clr.green, clr.blue)

                    return@onComplete AnvilGUI.Response.close()

                } catch (ex: NumberFormatException) {
                    msg.sendMessage(pl, "invalid-hex")
                    return@onComplete AnvilGUI.Response.close()
                }
            }
            .plugin(plugin)
            .title("Set trail hex color")
            .onClose { this.openMenu() }
            .text(formatToHex(data.getOrSetColor(pl, null)))
            .open(pl)
    }

    fun openMenu() {
        if (this.isInvalid)
            this.buildGui()

        container.openFor(player)
    }

    private val isInvalid: Boolean
        get() = !this.framework.guiManager.activeGuis.contains(container)


    enum class ColorValue(val colorName: String, val material: Material, val color: Color, val slot: Int) {
        RED("&cRed", Material.RED_DYE, Color.RED, 10),
        ORANGE("&6Orange", Material.ORANGE_DYE, Color.ORANGE, 11),
        YELLOW("&eYellow", Material.YELLOW_DYE, Color.YELLOW, 12),
        LIME("&aLime", Material.LIME_DYE, Color.LIME, 13),
        GREEN("&2Green", Material.GREEN_DYE, Color.GREEN, 14),
        LIGHT_BLUE("&bLight Blue", Material.LIGHT_BLUE_DYE, Color.AQUA, 15),
        CYAN("&3Cyan", Material.CYAN_DYE, Color.TEAL, 16),
        BLUE("&9Blue", Material.BLUE_DYE, Color.BLUE, 19),
        PURPLE("&5Purple", Material.PURPLE_DYE, Color.PURPLE, 20),
        PINK("&dPink", Material.PINK_DYE, Color.fromRGB(255, 182, 193), 21),
        WHITE("&fWhite", Material.WHITE_DYE, Color.WHITE, 22),
        LIGHT_GRAY("&7Light Gray", Material.LIGHT_GRAY_DYE, Color.SILVER, 23),
        GRAY("&8Gray", Material.GRAY_DYE, Color.GRAY, 24),
        BLACK("&8&lBlack", Material.BLACK_DYE, Color.BLACK, 25)
    }

    private enum class RGBType {
        RED, GREEN, BLUE
    }
}