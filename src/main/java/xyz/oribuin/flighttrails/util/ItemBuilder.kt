package xyz.oribuin.flighttrails.util

import com.google.common.collect.Multimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.banner.Pattern
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.map.MapView
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import xyz.oribuin.flighttrails.util.HexUtils.colorify
import java.util.*

/**
 * @author Oribuin
 */
class ItemBuilder(private val itemStack: ItemStack = ItemStack(Material.AIR), private val debug: Boolean = false) {

    /**
     * Set the ItemStack material
     *
     * @param material The material being set.
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setMaterial(material: Material): ItemBuilder {
        itemStack.type = material
        return this
    }

    /**
     * Set the ItemStack amount
     *
     * @param amount The amount of ItemStack
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setAmount(amount: Int): ItemBuilder {
        itemStack.amount = amount
        return this
    }

    /**
     * Set the name of the Item
     *
     * @param string The name being set
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setName(string: String): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        meta.setDisplayName(colorify(string))
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Add enchantments to the Item
     *
     * @param enchants A list of enchantments being added.
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun addEnchants(vararg enchants: Enchant): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this

        enchants.forEach { meta.addEnchant(it.enchantment, it.level, true) }
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set the lore of the item
     *
     * @param lore The lore being set
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setLore(lore: List<String>): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        val newLore = mutableListOf<String>()
        lore.forEach { newLore.add(colorify(it)) }
        meta.lore = newLore
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set the item flags
     *
     * @param flags The ItemFlags being added.
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun addFlags(vararg flags: ItemFlag): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        meta.addItemFlags(*flags)
        itemStack.itemMeta = meta
        return this
    }

    @Suppress("UNUSED")
    fun setGlowing(glowing: Boolean): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (glowing) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, true)
            itemStack.itemMeta = meta
        }

        return this
    }

    /**
     * Set skull owner, This requires the Material being a player head
     *
     * @param owner The owner of the skull
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setSkullOwner(owner: OfflinePlayer): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (itemStack.type != Material.PLAYER_HEAD || meta !is SkullMeta)
            return this

        meta.owningPlayer = owner
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set skull base64 properties
     *
     * @param base64 The base64 Value
     * @return ItemBuilder
     *
     * Uses imports
     * com.mojang.authlib.GameProfile
     * com.mojang.authlib.properties.Property
     */
    @Suppress("UNUSED")
    fun setSkull64(base64: String): ItemBuilder {
        val startTime = System.nanoTime()

        val meta = itemStack.itemMeta ?: return this
        if (itemStack.type != Material.PLAYER_HEAD || meta !is SkullMeta)
            return this

        val profile = GameProfile(UUID.randomUUID(), "")
        profile.properties.put("textures", Property("textures", base64))
        val field = meta.javaClass.getDeclaredField("profile")
        field.isAccessible = true
        field.set(meta, profile)
        itemStack.itemMeta = meta
        val endTime = System.nanoTime()
        if (debug) {
            println("Set Base64 Meta in ${(endTime - startTime) / 1e6}ms")
        }

        return this
    }

    /**
     * Set the item as unbreakable or not
     *
     * @param unbreakable true if the item is unbreakable
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        meta.isUnbreakable = unbreakable
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set attribute modifiers
     *
     * @param modifiers MultiMap of modifiers
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setAttributes(modifiers: Multimap<Attribute, AttributeModifier>): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        meta.attributeModifiers = modifiers
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set the leather armor color, Requires leather armor
     *
     * @param color The color of the armor
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setColor(color: Color): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (meta !is LeatherArmorMeta)
            return this

        meta.setColor(color)
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set banner patterns
     *
     * @param patterns The list of patterns on the banner
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setBannerPatterns(patterns: List<Pattern>): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (meta !is BannerMeta)
            return this

        meta.patterns = patterns
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set compass lodestone
     *
     * @param lodestone The location of the lodestone
     * @param tracked If the compass is tracking a specific lodestone
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setLodestone(lodestone: Location, tracked: Boolean): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (meta !is CompassMeta)
            return this

        meta.lodestone = lodestone
        meta.isLodestoneTracked = tracked
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set crossbow projectiles
     *
     * @param projectiles The projectiles to be charged
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setProjectile(projectiles: MutableList<ItemStack>): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (meta !is CrossbowMeta)
            return this

        meta.setChargedProjectiles(projectiles)
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set firework values
     *
     * @param effects List of firework effects this has
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setFirework(effects: List<FireworkEffect>?, power: Int): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (meta !is FireworkMeta)
            return this

        if (effects != null) {
            meta.addEffects(*effects.toTypedArray())
        }

        meta.power = power
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Create a map using values
     *
     * @param color Set the map color
     * @param locationName Set the name of the location
     * @param mapView Set the view associated with the map
     * @param scaling Should the map be scaled
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setMap(color: Color, locationName: String, mapView: MapView, scaling: Boolean): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (meta !is MapMeta)
            return this

        meta.color = color
        meta.isScaling = scaling
        meta.mapView = mapView
        meta.locationName = locationName
        itemStack.itemMeta = meta
        return this
    }

    /**
     * Set the potion meta
     *
     * @param effects the effects the potion has
     * @param color The color of the potion
     * @param potionData The base potion data, can be null
     * @return ItemBuilder
     */
    @Suppress("UNUSED")
    fun setPotion(effects: List<Effect>, color: Color, potionData: PotionData?): ItemBuilder {
        val meta = itemStack.itemMeta ?: return this
        if (meta !is PotionMeta)
            return this

        effects.forEach { meta.addCustomEffect(it.effect, it.overwrite) }
        meta.color = color
        if (potionData != null) {
            meta.basePotionData = potionData
        }

        itemStack.itemMeta = meta
        return this
    }

    @Suppress("UNUSED")
    fun build(): ItemStack {
        return itemStack
    }

    class Enchant(val enchantment: Enchantment, val level: Int)

    class Effect(val effect: PotionEffect, val overwrite: Boolean)
}