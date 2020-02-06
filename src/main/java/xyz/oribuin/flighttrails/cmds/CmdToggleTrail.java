package xyz.oribuin.flighttrails.cmds;

import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import xyz.oribuin.flighttrails.persist.Chat;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CmdToggleTrail implements CommandExecutor {
    FlightTrails plugin;
    FlyHandler flyHandler;


    public CmdToggleTrail(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
    }


    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), plugin.getConfig().getInt("particle-size"));
    }

    Particle.DustOptions color = null;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;
            FileConfiguration config = plugin.getConfig();
            /*
             If you does not have permission
             return No Permission Message
            */
            if (!player.hasPermission("flytrails.fly")) {
                player.sendMessage(Chat.cl(config.getString("prefix") + config.getString("no-permission")));
                return true;
            }

            // If they have trails disabled, Enable them
            if (!flyHandler.trailIsToggled(player.getUniqueId())) {
                player.sendMessage(Chat.cl(config.getString("prefix") + config.getString("trails-enabled")));
            } else {
                // If they have trails enabled, disable them.
                player.sendMessage(Chat.cl(config.getString("prefix") + config.getString("trails-disabled")));
            }

            flyHandler.trailToggle(player.getUniqueId());
        } else {
            sender.sendMessage(Chat.cl(plugin.getConfig().getString("prefix") + plugin.getConfig().getString("player-only")));
        }
        return true;
    }
}
