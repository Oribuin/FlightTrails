package xyz.oribuin.flighttrails.data;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.flighttrails.manager.ConfigManager.Setting;
import xyz.oribuin.flighttrails.util.TrailColor;

/**
 * @author Oribuin
 *
 * Contibutors: Esophose
 */
public class PlayerData {

    private final Player player;
    private boolean enabled;
    private Particle particle;
    private ItemStack item;
    private BlockData block;
    private TrailColor color;

    // Get all the PlayerData from data.yml
    public PlayerData(Player player, ConfigurationSection dataConfig) {
        this(player, dataConfig.getBoolean("enabled", true),
                dataConfig.getString("particle"),
                dataConfig.getString("item"),
                dataConfig.getString("block"),
                dataConfig.getString("color"));
    }

    // Get all the playerdata from config.yml
    private PlayerData(Player player) {
        this(player,
                Setting.PARTICLE_DEFAULT_ENABLED.getBoolean(),
                Setting.PARTICLE_DEFAULT_PARTICLE.getString(),
                Setting.PARTICLE_DEFAULT_ITEM.getString(),
                Setting.PARTICLE_DEFAULT_BLOCK.getString(),
                Setting.PARTICLE_DEFAULT_COLOR.getString());
    }

    // Get PlayerData Objects
    private PlayerData(Player player, boolean enabled, String particleValue, String itemValue, String blockValue, String colorValue) {
        this.player = player;
        this.enabled = enabled;

        // Get particle object
        if (particleValue == null) {
            this.particle = Particle.FLAME;
        } else {
            this.particle = Particle.valueOf(particleValue);
        }

        // Get item object
        Material itemMaterial;
        if (itemValue == null || (itemMaterial = Material.getMaterial(itemValue)) == null) {
            this.item = new ItemStack(Material.DIAMOND);
        } else {
            this.item = new ItemStack(itemMaterial);
        }

        // Get block object
        Material blockMaterial;
        if (blockValue == null || (blockMaterial = Material.getMaterial(blockValue)) == null) {
            this.block = Material.DIAMOND_BLOCK.createBlockData();
        } else {
            this.block = blockMaterial.createBlockData();
        }

        // Get color object
        if (colorValue == null) {
            this.color = TrailColor.WHITE;
        } else {
            this.color = TrailColor.valueOf(colorValue);
        }
    }

    // Get default PlayerData values
    public static PlayerData getDefault(Player player) {
        return new PlayerData(player);
    }

    // Return player
    public Player getPlayer() {
        return this.player;
    }

    // Return true if enabled
    public boolean isEnabled() {
        return this.enabled;
    }

    // Set PlayerData as enabled
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // Return particle
    public Particle getParticle() {
        return this.particle;
    }

    // Set PlayerData particle
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    // Return ItemStack
    public ItemStack getItem() {
        return this.item;
    }

    // Set PlayerData ItemStack
    public void setItem(ItemStack item) {
        this.item = item;
    }

    // Return PlayerData block
    public BlockData getBlock() {
        return this.block;
    }

    // Set PlayerData block
    public void setBlock(BlockData block) {
        this.block = block;
    }

    // Return TrailColor
    public TrailColor getTrailColor() {
        return this.color;
    }

    // Set TrailColor
    public void setTrailColor(TrailColor color) {
        this.color = color;
    }

}
