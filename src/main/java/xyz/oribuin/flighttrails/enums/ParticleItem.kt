package xyz.oribuin.flighttrails.enums

import org.bukkit.Material
import org.bukkit.Particle

enum class ParticleItem(val particleName: String, val particle: Particle, val material: Material) {
    ASH("Ash", Particle.ASH, Material.BASALT),
    BARRIER("Barrier", Particle.BARRIER, Material.BARRIER),
    BLOCK_CRACK("Block Crack", Particle.BLOCK_CRACK, Material.OAK_PLANKS),
    BUBBLE_COLUMN_UP("Bubble Up", Particle.BUBBLE_COLUMN_UP, Material.SOUL_SAND),
    BUBBLE_POP("Bubble Pop", Particle.BUBBLE_POP, Material.BUBBLE_CORAL),
    CAMPFIRE_COSY("Campfire Cosy Smoke", Particle.CAMPFIRE_COSY_SMOKE, Material.CAMPFIRE),
    CAMPFIRE_SIGNAL("Campfire Signal Smoke", Particle.CAMPFIRE_SIGNAL_SMOKE, Material.SOUL_CAMPFIRE),
    CLOUD("Cloud", Particle.CLOUD, Material.WHITE_WOOL),
    COMPOSTER("Composter", Particle.COMPOSTER, Material.COMPOSTER),
    CRIMSON_SPORE("Crimson Spore", Particle.CRIMSON_SPORE, Material.CRIMSON_FUNGUS),
    CRIT("Crit", Particle.CRIT, Material.DIAMOND_SWORD),
    CRIT_MAGIC("Crit Magic", Particle.CRIT_MAGIC, Material.DIAMOND_AXE),
    CURRENT_DOWN("Current Down", Particle.CURRENT_DOWN, Material.SOUL_SAND),
    DAMAGE_INDICATOR("Damage Indicator", Particle.DAMAGE_INDICATOR, Material.GOLDEN_AXE),
    DOLPHIN("Dolphin", Particle.DOLPHIN, Material.DOLPHIN_SPAWN_EGG),
    DUST("Dust", Particle.REDSTONE, Material.REDSTONE),
    DRAGON_BREATH("Dragon Breath", Particle.DRAGON_BREATH, Material.DRAGON_BREATH),
    DRIP_LAVA("Drip Lava", Particle.DRIP_LAVA, Material.LAVA_BUCKET),
    DRIP_WATER("Drip Water", Particle.DRIP_WATER, Material.WATER_BUCKET),
    DRIPPING_HONEY("Dripping Honey", Particle.DRIPPING_HONEY, Material.HONEY_BLOCK),
    DRIPPING_OBSIDIAN_TEAR("Dripping Obsidian Tear", Particle.DRIPPING_OBSIDIAN_TEAR, Material.CRYING_OBSIDIAN),
    ENCHANTMENT_TABLE("Enchantment Table", Particle.ENCHANTMENT_TABLE, Material.ENCHANTING_TABLE),
    END_ROD("End Rod", Particle.END_ROD, Material.END_ROD),
    EXPLOSION_HUGE("Explosion Huge", Particle.EXPLOSION_HUGE, Material.TNT),
    EXPLOSION_LARGE("Explosion Large", Particle.EXPLOSION_LARGE, Material.TNT_MINECART),
    EXPLOSION_NORMAL("Explosion Normal", Particle.EXPLOSION_NORMAL, Material.END_CRYSTAL),
    FALLING_DUST("Falling Dust", Particle.FALLING_DUST, Material.BLUE_CONCRETE_POWDER),
    FALLING_HONEY("Falling Honey", Particle.FALLING_HONEY, Material.BEEHIVE),
    FALLING_LAVA("Falling Lava", Particle.FALLING_LAVA, Material.RED_DYE),
    FALLING_NECTAR("Falling Nectar", Particle.FALLING_NECTAR, Material.BEE_NEST),
    FALLING_OBSIDIAN_TEAR("Falling Obsidian Tear", Particle.FALLING_OBSIDIAN_TEAR, Material.ANCIENT_DEBRIS),
    FALLING_WATER("Falling Water", Particle.FALLING_WATER, Material.BLUE_DYE),
    FIREWORK("Firework", Particle.FIREWORKS_SPARK, Material.FIREWORK_ROCKET),
    FLAME("Flame", Particle.FLAME, Material.TORCH),
    HEART("Heart", Particle.HEART, Material.POPPY),
    ITEM_CRACK("Item Crack", Particle.ITEM_CRACK, Material.STICK),
    LANDING_HONEY("Landing Honey", Particle.LANDING_HONEY, Material.HONEYCOMB_BLOCK),
    LANDING_LAVA("Landing Lava", Particle.LANDING_LAVA, Material.ORANGE_DYE),
    LANDING_OBSIDIAN_TEAR("Landing Obsidian Tear", Particle.LANDING_OBSIDIAN_TEAR, Material.OBSIDIAN),
    LAVA("Lava", Particle.LAVA, Material.MAGMA_CREAM),

    // WHY IS MYCELIUM PARTICLE CALLED TOWN AURA
    MYCELIUM("Mycelium", Particle.TOWN_AURA, Material.MYCELIUM),

    NAUTILIS("Nautilis", Particle.NAUTILUS, Material.HEART_OF_THE_SEA),
    NOTE("Note", Particle.NOTE, Material.NOTE_BLOCK),
    PORTAL("Portal", Particle.PORTAL, Material.PURPLE_CONCRETE),
    REVERSED_PORTAL("Reversed Portal", Particle.REVERSE_PORTAL, Material.PURPLE_DYE),
    SLIME("Slime", Particle.SLIME, Material.SLIME_BLOCK),
    SMOKE_LARGE("Smoke Large", Particle.SMOKE_LARGE, Material.NETHERITE_BLOCK),
    SMOKE_NORMAL("Smoke Normal", Particle.SMOKE_NORMAL, Material.COBWEB),
    SNEEZE("Sneeze", Particle.SNEEZE, Material.BAMBOO),
    SNOW_SHOVEL("Snow Shovel", Particle.SNOW_SHOVEL, Material.SNOWBALL),
    SOUL("Soul", Particle.SOUL, Material.SOUL_SOIL),
    SOUL_FIRE("Soul Fire", Particle.SOUL_FIRE_FLAME, Material.SOUL_TORCH),
    SPELL("Spell", Particle.SPELL, Material.POTION),
    SPELL_INSTANT("Spell Instant", Particle.SPELL_INSTANT, Material.GLOWSTONE_DUST),
    SPELL_MOB("Spell Mob", Particle.SPELL_MOB, Material.GUNPOWDER),
    SPELL_AMBIENT("Spell Ambient", Particle.SPELL_MOB_AMBIENT, Material.BEACON),
    SPIT("Spit", Particle.SPIT, Material.LLAMA_SPAWN_EGG),
    SQUID_INK("Squid Ink", Particle.SQUID_INK, Material.INK_SAC),
    SWEEP("Sweep", Particle.SWEEP_ATTACK, Material.GOLDEN_SWORD),
    TOTEM("Totem", Particle.TOTEM, Material.TOTEM_OF_UNDYING),
    VILLAGER_HAPPY("Villager Happy", Particle.VILLAGER_HAPPY, Material.EMERALD),
    VILLAGER_ANGY("Villager Angry", Particle.VILLAGER_ANGRY, Material.WITHER_ROSE),
    WARPED_SPORE("Warped Spore", Particle.WARPED_SPORE, Material.WARPED_FUNGUS),
    WATER_DROP("Rain", Particle.WATER_DROP, Material.COD_BUCKET),
    WATER_SPLASH("Splash", Particle.WATER_SPLASH, Material.PUFFERFISH_BUCKET),
    WAKE("Wake", Particle.WATER_WAKE, Material.LIGHT_BLUE_DYE),
    WHITE_ASH("White Ash", Particle.WHITE_ASH, Material.POLISHED_BASALT),
    WITCH("Witch", Particle.SPELL_WITCH, Material.SPLASH_POTION),
}