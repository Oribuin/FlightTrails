package xyz.oribuin.flighttrails.menu

import dev.rosewood.guiframework.GuiFactory
import dev.rosewood.guiframework.GuiFramework
import dev.rosewood.guiframework.gui.ClickAction
import dev.rosewood.guiframework.gui.GuiSize
import dev.rosewood.guiframework.gui.screen.GuiScreen
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.library.HexUtils.colorify
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.StringPlaceholders
import java.util.*
import java.util.function.Function

class BlockMenu(private val plugin: FlightTrails, private val player: Player) : Listener {

    private val guiFramework = GuiFramework.instantiate(plugin)
    private val container = GuiFactory.createContainer().setTickRate(3)

    companion object {
        var instance: BlockMenu? = null
            private set
    }

    private fun buildGui() {
        container.addScreen(mainMenu())
        guiFramework.guiManager.registerGui(container)
    }

    private fun mainMenu(): GuiScreen {
        val screen = GuiFactory.createScreen(container, GuiSize.ROWS_SIX)
            .setTitle(colorify("Set your block"))

        this.borderSlots().forEach { i -> screen.addItemStackAt(i, borderItem()) }

        val blockList = mutableListOf<Material>()

        val tempInv = Bukkit.createInventory(null, 9)
        for (material in Material.values()) {
            tempInv.clear()
            tempInv.setItem(0, ItemStack(material, 1))
            val stack = tempInv.getItem(0)
            if (stack != null) {
                if (material.isBlock) {
                    blockList.add(material)
                }
            }
        }

        screen.addButtonAt(47, GuiFactory.createButton()
            .setIcon(Material.PAPER)
            .setGlowing(true)
            .setHiddenReplacement(borderItem())
            .setName(format("&7«- &bBack Page"))
            .setClickAction(Function { return@Function ClickAction.PAGE_BACKWARDS })
        )


        screen.addButtonAt(51, GuiFactory.createButton()
            .setIcon(Material.PAPER)
            .setGlowing(true)
            .setHiddenReplacement(borderItem())
            .setName(format("&bForward Page &7»"))
            .setClickAction(Function { return@Function ClickAction.PAGE_FORWARDS })
        )

        screen.setPaginatedSection(GuiFactory.createScreenSection(particleSlots()), blockList.size) { _: Int, startIndex: Int, endIndex: Int ->
            val results = GuiFactory.createPageContentsResult()
            for (i in startIndex until endIndex.coerceAtMost(blockList.size)) {

                val blockItem = blockList[i]

                val button = GuiFactory.createButton()
                    .setIcon(blockItem)
                    .setName(format("&b${StringUtils.capitalize(blockItem.name.toLowerCase().replace("_", " "))}"))
                    .setLore(format("&7Click to change your current"), format("&7flight trail block."))
                    .setClickAction(Function {
                        blockCommands(player, blockItem)
                        return@Function ClickAction.CLOSE
                    })

                if (plugin.getManager(DataManager::class.java).getOrSetBlock(player, null) == blockItem) {
                    button.setGlowing(true)
                }

                results.addPageContent(button)
            }

            return@setPaginatedSection results
        }
        return screen
    }

    private fun borderItem(): ItemStack {
        val stack = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val meta = stack.itemMeta ?: return ItemStack(Material.AIR)

        meta.setDisplayName(" ")
        stack.itemMeta = meta
        return stack
    }

    private fun borderSlots(): List<Int> {
        val slots: MutableList<Int> = ArrayList()
        for (i in 0..8) slots.add(i)
        slots.add(9)
        slots.add(17)
        slots.add(18)
        slots.add(26)
        slots.add(27)
        slots.add(35)
        slots.add(36)
        slots.add(44)
        for (i in 45..53) slots.add(i)
        return slots
    }

    private fun particleSlots(): List<Int> {
        val slots: MutableList<Int> = ArrayList()
        for (i in 10..16) slots.add(i)
        for (i in 19..25) slots.add(i)
        for (i in 28..34) slots.add(i)
        for (i in 37..43) slots.add(i)
        return slots
    }

    fun openMenu() {
        if (isInvalid)
            buildGui()

        container.openFor(player)
    }

    private val isInvalid: Boolean
        get() = !guiFramework.guiManager.activeGuis.contains(container)


    private fun format(string: String, placeholders: StringPlaceholders = StringPlaceholders.empty()): String {
        return colorify(placeholders.apply(string))
    }

    private fun blockCommands(player: Player, material: Material) {
        val msg = plugin.getManager(MessageManager::class.java)
        val data = plugin.getManager(DataManager::class.java)
//
//        if (!player.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle.${material.name.toLowerCase()}")) {
//            msg.sendMessage(player, "invalid-permission")
//            player.closeInventory()
//        }

        msg.sendMessage(
            player, "set-command.block", StringPlaceholders.builder()
                .addPlaceholder("block", StringUtils.capitalize(material.name.toLowerCase().replace("_", " ")))
                .build()
        )

        data.getOrSetBlock(player, material)
    }
}