package me.oribuin.flighttrails.cmds;

import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.handlers.FlyHandler;
import me.oribuin.flighttrails.menus.ColorSelector;
import me.oribuin.flighttrails.persist.ColorU;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CmdSetColor implements CommandExecutor {

    public FlyHandler flyHandler;
    FlightTrails plugin;

    public CmdSetColor(FlyHandler flyHandler, FlightTrails instance) {
        this.flyHandler = flyHandler;
        this.plugin = instance;
    }


    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), plugin.getConfig().getInt("particle-size"));
    }

    Particle.DustOptions color = null;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            FileConfiguration config = plugin.getConfig();

            if (!player.hasPermission("flytrails.color.custom")) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("no-permission")));
                return true;
            }

            if (args.length <= 2) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-args")));
                return true;
            }

            int r = Integer.parseInt(args[0]);
            int g = Integer.parseInt(args[1]);
            int b = Integer.parseInt(args[2]);

            if (r > 255 || r < 0
                    || g > 255 || g < 0
                    || b > 255 || b < 0) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-int")));
                return true;
            }

            color = particleColor(r, g, b);

            if (color != null) {
                ColorSelector.dustOptionsMap.put(player.getUniqueId(), color);
            }

            player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("custom-color-message")
                    .replaceAll("\\{r}", "" + color.getColor().getRed())
                    .replaceAll("\\{g}", "" + color.getColor().getGreen())
                    .replaceAll("\\{b}", "" + color.getColor().getBlue())
            ));
        }
        return true;
    }
}
