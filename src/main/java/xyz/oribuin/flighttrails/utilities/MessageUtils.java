package xyz.oribuin.flighttrails.utilities;

import com.google.common.util.concurrent.FakeTimeLimiter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.flighttrails.FlightTrails;

import java.io.File;

public class MessageUtils {
    private CommandSender sender;
    private Player player;

    public MessageUtils(CommandSender sender) {
        this.sender = sender;
    }

    public MessageUtils(Player player) {
        this.player = player;
    }

    public MessageUtils(CommandSender sender, Player player) {
        this.sender = sender;
        this.player = player;
    }

    public String getMessage(String msgTile) {
        switch (msgTile) {
            case "invalidPermission":
                return Color.msg(prefix() + getMsgs().getString("invalid-permission"));
            case "reload":
                return Color.msg(prefix() + getMsgs().getString("reload").replaceAll("%version%", FlightTrails.getInstance().getDescription().getVersion()));
            case "trailsEnabled":
                return Color.msg(prefix() + getMsgs().getString("trails-enabled"));
            case "trailsDisabled":
                return Color.msg(prefix() + getMsgs().getString("trails-disabled"));

        }

        return msgTile;
    }

    public String getUsage(String command) {
        return Color.msg(prefix() + getMsgs().getString("invalid-args").replaceAll("%usage%", FlightTrails.getInstance().getCommand(command).getUsage()));
    }

    public FileConfiguration getMsgs() {
        return YamlConfiguration.loadConfiguration(new File(FlightTrails.getInstance().getDataFolder(), "messages.yml"));
    }

    private String prefix() {
        return Color.msg(getMsgs().getString("prefix"));
    }
}