package xyz.oribuin.flighttrails.data;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.flighttrails.manager.ConfigManager.Setting;
import xyz.oribuin.flighttrails.util.TrailColor;

public class PlayerData {

    private Player player;
    private boolean enabled;
    private Particle particle;
    private ItemStack item;
    private BlockData block;
    private TrailColor color;

    public PlayerData(Player player, ConfigurationSection dataConfig) {
        this(player, dataConfig.getBoolean("enabled", true),
             dataConfig.getString("particle"),
             dataConfig.getString("item"),
             dataConfig.getString("block"),
             dataConfig.getString("color"));
    }

    private PlayerData(Player player) {
        this(player,
             Setting.PARTICLE_DEFAULT_ENABLED.getBoolean(),
             Setting.PARTICLE_DEFAULT_PARTICLE.getString(),
             Setting.PARTICLE_DEFAULT_ITEM.getString(),
             Setting.PARTICLE_DEFAULT_BLOCK.getString(),
             Setting.PARTICLE_DEFAULT_COLOR.getString());
    }

    private PlayerData(Player player, boolean enabled, String particleValue, String itemValue, String blockValue, String colorValue) {
        this.player = player;
        this.enabled = enabled;

        if (particleValue == null) {
            this.particle = Particle.FLAME;
        } else {
            this.particle = Particle.valueOf(particleValue);
        }

        Material itemMaterial;
        if (itemValue == null || (itemMaterial = Material.getMaterial(itemValue)) == null) {
            this.item = new ItemStack(Material.DIAMOND);
        } else {
            this.item = new ItemStack(itemMaterial);
        }

        Material blockMaterial;
        if (blockValue == null || (blockMaterial = Material.getMaterial(blockValue)) == null) {
            this.block = Material.DIAMOND_BLOCK.createBlockData();
        } else {
            this.block = blockMaterial.createBlockData();
        }

        if (colorValue == null) {
            this.color = TrailColor.WHITE;
        } else {
            this.color = TrailColor.valueOf(colorValue);
        }
    }

    public static PlayerData getDefault(Player player) {
        return new PlayerData(player);
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Particle getParticle() {
        return this.particle;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public BlockData getBlock() {
        return this.block;
    }

    public TrailColor getTrailColor() {
        return this.color;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setBlock(BlockData block) {
        this.block = block;
    }

    public void setTrailColor(TrailColor color) {
        this.color = color;
    }

}
