package xyz.oribuin.flighttrails;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import xyz.oribuin.flighttrails.data.PlayerData;
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook;
import xyz.oribuin.flighttrails.hook.PlaceholderExpansion;
import xyz.oribuin.flighttrails.manager.CommandManager;
import xyz.oribuin.flighttrails.manager.ConfigManager;
import xyz.oribuin.flighttrails.manager.ConfigManager.Setting;
import xyz.oribuin.flighttrails.manager.DataManager;
import xyz.oribuin.flighttrails.manager.MessageManager;

public class FlightTrails extends JavaPlugin {

    private static FlightTrails instance;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private DataManager dataManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;
        this.commandManager = new CommandManager(this);
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);
        this.messageManager = new MessageManager(this);

        this.reload();

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getConsoleSender().sendMessage("[FlightTrails] No PlaceholderAPI, Placeholders will not work.");
        }

        if (PlaceholderAPIHook.enabled())
            new PlaceholderExpansion(this).register();

            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                        List<String> disabledWorlds = Setting.DISABLED_WORLDS.getStringList();
                        for (PlayerData playerData : this.dataManager.getAllPlayerData()) {
                            Player player = playerData.getPlayer();
                            if (!player.hasPermission("flighttrails.use")
                                    || player.hasMetadata("vanished")
                                    || !playerData.isEnabled()
                                    || disabledWorlds.contains(player.getWorld().getName())) {
                                continue;
                            }

                            if ((Setting.CREATIVE_FLY_ENABLED.getBoolean() && player.isFlying())
                                    || (Setting.ELYTRA_ENABLED.getBoolean() && player.isGliding())) {
                                this.spawnParticles(playerData);
                            }
                        }
                    }, 0, 1);
    }

    public void spawnParticles(PlayerData playerData) {
        Player player = playerData.getPlayer();
        Particle particle = playerData.getParticle();
        int particleCount = Setting.PARTICLE_COUNT.getInt();

        switch (particle) {
            case REDSTONE:
                Particle.DustOptions color = new Particle.DustOptions(playerData.getTrailColor().getColor(), Setting.PARTICLE_SIZE.getFloat());
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, color);
                break;
            case BLOCK_CRACK:
            case BLOCK_DUST:
            case FALLING_DUST:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, playerData.getBlock());
                break;
            case ITEM_CRACK:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, playerData.getItem());
                break;
            default:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, 0);
                break;
        }
    }

    public void reload() {
        this.commandManager.reload();
        this.configManager.reload();
        this.dataManager.reload();
        this.messageManager.reload();
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }

    public static FlightTrails getInstance() {
        return instance;
    }

}
