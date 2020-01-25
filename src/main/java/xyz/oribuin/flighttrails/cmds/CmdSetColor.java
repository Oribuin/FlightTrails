package xyz.oribuin.flighttrails.cmds;

import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import xyz.oribuin.flighttrails.persist.ColorU;
import xyz.oribuin.flighttrails.persist.Data;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CmdSetColor implements CommandExecutor {
    FlightTrails plugin;
    FlyHandler flyHandler;

    public CmdSetColor(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
    }

    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), plugin.getConfig().getInt("particle-size"));
    }

    Particle.DustOptions color = null;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration config = plugin.getConfig();
        String[] tabComplete = {
                "<0-255>",
                "<0-255>",
                "<0-255>"
        };

        if (sender instanceof Player) {
            Player player = (Player) sender;
            try {
                if (!player.hasPermission("flytrails.color")) {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("no-permission").replaceAll("\\{usage}", command.getUsage())));
                    return true;
                }

                if (args.length <= 2) {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-args").replaceAll("\\{usage}", command.getUsage())));
                    return true;
                }

                int r = Integer.parseInt(args[0]);
                int g = Integer.parseInt(args[1]);
                int b = Integer.parseInt(args[2]);


                // If the integers are higher than 255 or lower than 0
                if (r > 255 || r < 0
                        || g > 255 || g < 0
                        || b > 255 || b < 0) {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-int")));
                    return true;
                }

                color = particleColor(r, g, b);

                // If the user does not have trails enabled, enable them.
                if (!flyHandler.trailIsToggled(player.getUniqueId()))
                    flyHandler.trailToggle(player.getUniqueId());

                // If color isnt null, put them on the dustmap
                if (color != null) {
                    if (Data.dustOptionsMap.get(player.getUniqueId()) != null) {
                        Data.dustOptionsMap.remove(player.getUniqueId());
                    }
                    Data.dustOptionsMap.put(player.getUniqueId(), color);
                }

                // Tell the player the color they just set.
                player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("custom-color-message")
                        .replaceAll("\\{player}", player.getName())
                        .replaceAll("\\{r}", "" + color.getColor().getRed())
                        .replaceAll("\\{g}", "" + color.getColor().getGreen())
                        .replaceAll("\\{b}", "" + color.getColor().getBlue())
                ));
            } catch (NumberFormatException exception) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-int")));
                return true;
            }
        } else {
            sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("player-only")));
            return true;
        }

        command.tabComplete(sender, command.getName(), tabComplete);
        return true;
    }
}