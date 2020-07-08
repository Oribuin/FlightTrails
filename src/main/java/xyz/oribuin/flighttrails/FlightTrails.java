package xyz.oribuin.flighttrails;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import xyz.oribuin.flighttrails.data.PlayerData;
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook;
import xyz.oribuin.flighttrails.hook.PlaceholderExp;
import xyz.oribuin.flighttrails.manager.CommandManager;
import xyz.oribuin.flighttrails.manager.ConfigManager;
import xyz.oribuin.flighttrails.manager.ConfigManager.Setting;
import xyz.oribuin.flighttrails.manager.DataManager;
import xyz.oribuin.flighttrails.manager.MessageManager;
import xyz.oribuin.flighttrails.util.HexUtils;
import xyz.oribuin.flighttrails.util.VectorUtils;

import java.util.List;

/**
 * @author Oribuin
 *
 * Contibutors: Esophose
 */
public class FlightTrails extends JavaPlugin {

    private static FlightTrails instance;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private DataManager dataManager;
    private MessageManager messageManager;

    public static FlightTrails getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Instantiate Managers
        this.commandManager = new CommandManager(this);
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.messageManager = new MessageManager(this);

        // Reload manages
        this.reload();

        // Warn PlaceholderAPI not being enabled.
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getConsoleSender().spigot().sendMessage(TextComponent.fromLegacyText(HexUtils.colorify("[FlightTrails] No PlaceholderAPI, Placeholders will not work.")));
        }

        // If PlaceholderAPIHook is enabled, register Placeholders
        if (PlaceholderAPIHook.enabled())
            new PlaceholderExp(this).register();

        // Run async timer to spawn particles
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {

            // Get disabled world list
            List<String> disabledWorlds = Setting.DISABLED_WORLDS.getStringList();

            // Go through all the PlayerData
            for (PlayerData playerData : this.dataManager.getAllPlayerData()) {

                // Get the player
                Player player = playerData.getPlayer();

                /*
                 * if the player does not have permission
                 * if the player is vanished
                 * if the player does not have trails enabled
                 * if the player is in a disabled world
                 * if the player is in spectator mode
                 *
                 * don't spawn particles
                 */

                if (!player.hasPermission("flighttrails.use")
                        || player.hasMetadata("vanished")
                        || !playerData.isEnabled()
                        || disabledWorlds.contains(player.getWorld().getName())
                        || player.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }

                // If creative flight particles are enabled and player is flying
                if (Setting.CREATIVE_FLY_ENABLED.getBoolean() && player.isFlying()) {
                    this.spawnFeetParticles(playerData);

                    // if Elytra particles are enabled and the player is gliding
                } else if (Setting.ELYTRA_ENABLED.getBoolean() && player.isGliding()) {
                    // If they have 'LEGACY' style turned on, spawn feet particles
                    if (Setting.ELYTRA_STYLE.getString().equalsIgnoreCase("LEGACY"))
                        this.spawnFeetParticles(playerData);

                    // If they do not have Legacy enabled, spawn elytra particles
                    else
                        this.spawnElytraParticles(playerData);
                }
            }
        }, 0, 1);
    }


    private void spawnFeetParticles(PlayerData playerData) {
        // Get player
        Player player = playerData.getPlayer();

        // Get particle
        Particle particle = playerData.getParticle();

        // Get particle size
        int particleCount = Setting.PARTICLE_COUNT.getInt();

        switch (particle) {
            // Spawn Redstone particles with PlayerData's Color
            case REDSTONE:
                Particle.DustOptions color = new Particle.DustOptions(playerData.getTrailColor().getColor(), Setting.PARTICLE_SIZE.getFloat());
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, color);
                break;

            // Spawn Block_Crack, Block_Dust and Falling dust particles with PlayerData's Block
            case BLOCK_CRACK:
            case BLOCK_DUST:
            case FALLING_DUST:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, playerData.getBlock());
                break;

            // Spawn item crack particle with Player's Item
            case ITEM_CRACK:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, playerData.getItem());
                break;

            default:
                // Spawn normal particles at the feet
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, 0);
                break;
        }
    }

    private void spawnElytraParticles(PlayerData playerData) {
        // Get player
        Player player = playerData.getPlayer();
        // Get particle
        Particle particle = playerData.getParticle();

        // Get particle size
        int particleCount = Setting.PARTICLE_COUNT.getInt();

        // Get feet location
        Location location = player.getLocation();
        double distanceFromFeetCenter = 0.75;

        // Rotate feet vector to display particles behind the elytra
        Vector leftEye = VectorUtils.rotateVector(new Vector(-0.25, -0.4, distanceFromFeetCenter), location.getYaw(), location.getPitch());
        Vector rightEye = VectorUtils.rotateVector(new Vector(-0.25, -0.4, -distanceFromFeetCenter), location.getYaw(), location.getPitch());

        switch (particle) {

            // Spawn Redstone particles with PlayerData's Color
            case REDSTONE:
                Particle.DustOptions color = new Particle.DustOptions(playerData.getTrailColor().getColor(), Setting.PARTICLE_SIZE.getFloat());
                player.getWorld().spawnParticle(particle, location.clone().subtract(leftEye), particleCount, 0, 0, 0, color);
                player.getWorld().spawnParticle(particle, location.clone().subtract(rightEye), particleCount, 0, 0, 0, color);
                break;

            // Spawn Block_Crack, Block_Dust and Falling dust particles with PlayerData's Block
            case BLOCK_CRACK:
            case BLOCK_DUST:
            case FALLING_DUST:
                player.getWorld().spawnParticle(particle, location.clone().subtract(leftEye), particleCount, 0, 0, 0, playerData.getBlock());
                player.getWorld().spawnParticle(particle, location.clone().subtract(rightEye), particleCount, 0, 0, 0, playerData.getBlock());
                break;

            case ITEM_CRACK:
                // Spawn item crack particle with Player's Item
                player.getWorld().spawnParticle(particle, location.clone().subtract(leftEye), particleCount, 0, 0, 0, playerData.getItem());
                player.getWorld().spawnParticle(particle, location.clone().subtract(rightEye), particleCount, 0, 0, 0, playerData.getItem());
                break;

            default:
                // Spawn normal dual elytra particles
                player.getWorld().spawnParticle(particle, location.clone().subtract(leftEye), particleCount, 0, 0, 0, 0);
                player.getWorld().spawnParticle(particle, location.clone().subtract(rightEye), particleCount, 0, 0, 0, 0);
                break;
        }
    }

    // Reload all the managers
    public void reload() {
        this.commandManager.reload();
        this.configManager.reload();
        this.dataManager.reload();
        this.messageManager.reload();
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }

}
