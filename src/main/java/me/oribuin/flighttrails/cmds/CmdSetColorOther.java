package me.oribuin.flighttrails.cmds;

import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.handlers.FlyHandler;
import me.oribuin.flighttrails.persist.ColorU;
import me.oribuin.flighttrails.persist.Data;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class CmdSetColorOther implements CommandExecutor {

    FlightTrails plugin;
    FlyHandler flyHandler;

    public CmdSetColorOther(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
    }

    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), plugin.getConfig().getInt("particle-size"));
    }

    Particle.DustOptions color = null;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        String[] tabComplete = {
                "<Player>",
                "<0-255>",
                "<0-255>",
                "<0-255>"
        };


        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!player.hasPermission("flytrails.color.other") && !player.hasPermission("flytrails.color")) {
                    sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("no-permission")));
                    return true;
                }
            }

            if (args.length < 4) {
                sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-args").replaceAll("\\{usage}", command.getUsage())));
                return true;
            }

            String mplayer = args[0];
            Player pplayer = sender.getServer().getPlayer(mplayer);

            if (pplayer == null) {
                sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-user")));
                return true;
            }


            int r = Integer.parseInt(args[1]);
            int g = Integer.parseInt(args[2]);
            int b = Integer.parseInt(args[3]);

            if (r > 255 || r < 0
                    || g > 255 || g < 0
                    || b > 255 || b < 0) {
                sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-int")));
                return true;
            }

            color = particleColor(r, g, b);

            // If the user does not have trails enabled, enable them.
            if (!flyHandler.trailIsToggled(pplayer.getUniqueId()))
                flyHandler.trailToggle(pplayer.getUniqueId());

            if (color != null) {
                if (Data.dustOptionsMap.get(pplayer.getUniqueId()) != null) {
                    Data.dustOptionsMap.remove(pplayer.getUniqueId());
                }
                Data.dustOptionsMap.put(pplayer.getUniqueId(), color);
            }

            if (sender.getName().equals(pplayer.getName())) {
                sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("custom-color-message")
                        .replaceAll("\\{player}", sender.getName())
                        .replaceAll("\\{r}", "" + color.getColor().getRed())
                        .replaceAll("\\{g}", "" + color.getColor().getGreen())
                        .replaceAll("\\{b}", "" + color.getColor().getBlue())
                ));
                return true;
            }

            // tell the player if sudo-tellplayer is true
            if (config.getBoolean("sudo-tellplayer", true)) {
                // Tell the player the color they just set.
                pplayer.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("player-sudo-message")
                        .replaceAll("\\{mentioned}", pplayer.getName())
                        .replaceAll("\\{player}", sender.getName())
                        .replaceAll("\\{r}", "" + color.getColor().getRed())
                        .replaceAll("\\{g}", "" + color.getColor().getGreen())
                        .replaceAll("\\{b}", "" + color.getColor().getBlue())
                ));
            }

            sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("sender-sudo-message")
                    .replaceAll("\\{mentioned}", pplayer.getName())
                    .replaceAll("\\{player}", sender.getName())
                    .replaceAll("\\{r}", "" + color.getColor().getRed())
                    .replaceAll("\\{g}", "" + color.getColor().getGreen())
                    .replaceAll("\\{b}", "" + color.getColor().getBlue())
            ));

            command.tabComplete(sender, command.getName(), tabComplete);
        } catch (NumberFormatException exception) {
            sender.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("invalid-int")));
            return true;
        }
        return true;
    }
}
