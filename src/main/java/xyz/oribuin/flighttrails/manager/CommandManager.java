package xyz.oribuin.flighttrails.manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.data.PlayerData;
import xyz.oribuin.flighttrails.util.StringPlaceholders;
import xyz.oribuin.flighttrails.util.TrailColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Oribuin
 * <p>
 * Contibutors: Esophose
 */

public class CommandManager extends Manager implements TabExecutor {

    public CommandManager(FlightTrails plugin) {
        super(plugin);

        PluginCommand trailsCommand = this.plugin.getCommand("trails");
        if (trailsCommand != null) {
            // Set executor and tab complete
            trailsCommand.setExecutor(this);
            trailsCommand.setTabCompleter(this);
        }
    }

    @Override
    public void reload() {
        // Unused
    }

    private void onReloadCommand(CommandSender sender) {
        MessageManager messageManager = this.plugin.getMessageManager();
        // Check if the player has the permission to reload the plugin
        if (!sender.hasPermission("flighttrails.reload")) {
            messageManager.sendMessage(sender, "invalid-permission");
            return;
        }

        // Reload plugin managers
        this.plugin.reload();
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.getDescription().getVersion()));
    }

    private void onToggleCommand(Player player, boolean isNew) {
        // Instantiate the managers
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        // If isNew create all PlayerData for that user
        if (isNew) {
            dataManager.getPlayerData(player, true);
            messageManager.sendMessage(player, "trails-enabled");
            return;
        }

        // Get the player data
        PlayerData playerData = dataManager.getPlayerData(player, false);

        boolean enabled = !playerData.isEnabled();

        // Update player Data
        dataManager.updatePlayerData(player, enabled);

        // Send Messages
        if (enabled) {
            messageManager.sendMessage(player, "trails-enabled");
        } else {
            messageManager.sendMessage(player, "trails-disabled");
        }
    }

    private void onSetParticle(Player player, String particleValue) {
        // Instantiate the managers

        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        // Check /trails set particle permission
        if (!player.hasPermission("flighttrails.set.particle")) {
            messageManager.sendMessage(player, "invalid-permission");
            return;
        }

        Particle particle;
        // Check particle
        try {
            particle = Particle.valueOf(particleValue.toUpperCase());
        } catch (Exception ex) {
            messageManager.sendMessage(player, "set-command.invalid-particle");
            return;
        }

        // Check if player has permission for the particle
        if (!player.hasPermission("flighttrails.particle." + particle.name().toLowerCase())) {
            messageManager.sendMessage(player, "invalid-permission");
            return;
        }

        // Update PlayerData
        dataManager.updatePlayerData(player, particle);
        messageManager.sendMessage(player, "set-command.particle", StringPlaceholders.single("particle", particle.name().toLowerCase()));
    }

    private void onSetItem(Player player, String itemValue) {
        // Instantiate the managers
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        // Check permission
        if (!player.hasPermission("flighttrails.set.item")) {
            messageManager.sendMessage(player, "invalid-permission");
            return;
        }

        // Instantiate the PlayerData and Particle
        PlayerData playerData = dataManager.getPlayerData(player, true);
        Particle particle = playerData.getParticle();

        // Check required particle
        if (particle != Particle.ITEM_CRACK) {
            messageManager.sendMessage(player, "set-command.required-particle");
            return;
        }

        // Check material included
        Material material = Material.getMaterial(itemValue);
        if (material == null) {
            messageManager.sendMessage(player, "set-command.invalid-item");
            return;
        }

        // Update ItemStack inside PlayerData
        ItemStack item = new ItemStack(material);
        dataManager.updatePlayerData(player, item);

        // Send message
        messageManager.sendMessage(player, "set-command.item", StringPlaceholders.single("item", material.name().toLowerCase()));
    }

    private void onSetBlock(Player player, String blockValue) {
        // Instantiate the managers
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        // Check Permission
        if (!player.hasPermission("flighttrails.set.block")) {
            messageManager.sendMessage(player, "invalid-permission");
            return;
        }

        // Instantiate the PlayerData and Particle
        PlayerData playerData = dataManager.getPlayerData(player, true);
        Particle particle = playerData.getParticle();

        // Check the required particles
        if (particle != Particle.BLOCK_CRACK && particle != Particle.BLOCK_DUST && particle != Particle.FALLING_DUST) {
            messageManager.sendMessage(player, "set-command.required-particle");
            return;
        }

        // Check the required material
        Material material = Material.getMaterial(blockValue.toUpperCase());
        if (material == null) {
            messageManager.sendMessage(player, "set-command.invalid-block");
            return;
        }

        // Create block data for material
        BlockData block = material.createBlockData();

        // Update PlayerData
        dataManager.updatePlayerData(player, block);

        // Send Message
        messageManager.sendMessage(player, "set-command.block", StringPlaceholders.single("block", material.name().toLowerCase()));
    }

    private void onSetColor(Player player, String colorValue) {
        // Instantiate the managers
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        // Check permission
        if (!player.hasPermission("flighttrails.set.color")) {
            messageManager.sendMessage(player, "invalid-permission");
            return;
        }

        // Instantiate the PlayerData and Particle
        PlayerData playerData = dataManager.getPlayerData(player, true);
        Particle particle = playerData.getParticle();

        // Check required particle
        if (particle != Particle.REDSTONE) {
            messageManager.sendMessage(player, "set-command.required-particle");
            return;
        }

        // Check required Trail Color
        TrailColor color;
        try {
            color = TrailColor.valueOf(colorValue.toUpperCase());
        } catch (Exception ex) {
            messageManager.sendMessage(player, "set-command.invalid-color");
            return;
        }

        // Check if player has permission for the trail color
        if (!player.hasPermission("flighttrails.color." + particle.name().toLowerCase())) {
            messageManager.sendMessage(player, "invalid-permission");
            return;
        }

        // Update player data
        dataManager.updatePlayerData(player, color);

        // Send update message
        messageManager.sendMessage(player, "set-command.color", StringPlaceholders.single("color", color.name().toLowerCase()));
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if command != trails
        if (!command.getName().equalsIgnoreCase("trails"))
            return true;

        // Instantiate the MessageManager
        MessageManager messageManager = this.plugin.getMessageManager();

        // If the args.length == 0 or first argument equals toggle
        if (args.length <= 2 && args[0].equalsIgnoreCase("toggle")) {

            if (args.length == 2) {

                Player mentioned = Bukkit.getPlayer(args[1]);

                if (!sender.hasPermission("flighttrails.toggle.other")) {
                    messageManager.sendMessage(sender, "invalid-permission");
                    return true;
                }

                // Check if mentioned is null, offline, vanished
                if (mentioned == null || !mentioned.isOnline() || mentioned.hasMetadata("vanished")) {
                    // Send invalid player
                    messageManager.sendMessage(sender, "invalid-player");
                    return true;
                }

                PlayerData playerData = this.plugin.getDataManager().getPlayerData(mentioned, false);
                this.onToggleCommand(mentioned, playerData == null);

                if (playerData == null)
                    return true;

                if (playerData.isEnabled())
                    messageManager.sendMessage(sender, "toggled-other-disabled", StringPlaceholders.single("player", mentioned.getName()));
                else
                    messageManager.sendMessage(sender, "toggled-other-enabled", StringPlaceholders.single("player", mentioned.getName()));
                return true;
            }

            // Check if sender is a player
            if (!(sender instanceof Player)) {
                messageManager.sendMessage(sender, "player-only");
                return true;
            }

            // Check permission
            if (!sender.hasPermission("flighttrails.toggle")) {
                messageManager.sendMessage(sender, "invalid-permission");
                return true;
            }

            // Define player
            Player player = (Player) sender;

            // Fire toggleCommand
            this.onToggleCommand(player, this.plugin.getDataManager().getPlayerData(player, false) == null);
            return true;
        }

        switch (args[0].toLowerCase()) {

            // If the first argument = "reload", Fire reload command
            case "reload":
                this.onReloadCommand(sender);
                break;


            // If the first argument = "set"
            case "set":

                // If the argument length == 1 and no type or value was defined, send message
                if (args.length == 1) {
                    messageManager.sendMessage(sender, "set-command.no-type");
                    break;
                } else if (args.length == 2) {
                    messageManager.sendMessage(sender, "set-command.no-value");
                    break;
                }

                // if the args.length == 3
                if (args.length == 3) {

                    // check if player
                    if (!(sender instanceof Player)) {
                        messageManager.sendMessage(sender, "player-only");
                        break;
                    }

                    // Update sender's particle based on the arguments
                    Player player = (Player) sender;
                    switch (args[1].toLowerCase()) {
                        case "particle":
                            this.onSetParticle(player, args[2]);
                            break;
                        case "item":
                            this.onSetItem(player, args[2].toUpperCase());
                            break;
                        case "block":
                            this.onSetBlock(player, args[2].toUpperCase());
                            break;
                        case "color":
                            this.onSetColor(player, args[2]);
                            break;
                    }

                    break;
                }

                // if the args length includes a player name
                if (args.length == 4) {

                    // Define player's name
                    Player mentioned = Bukkit.getPlayer(args[3]);

                    // If sender has permission to change other's particles
                    if (!sender.hasPermission("flighttrails.set.other")) {
                        messageManager.sendMessage(sender, "invalid-permission");
                        return true;
                    }

                    // Check if mentioned is null, offline, vanished
                    if (mentioned == null || !mentioned.isOnline() || mentioned.hasMetadata("vanished")) {
                        // Send invalid player
                        messageManager.sendMessage(sender, "invalid-player");
                        return true;
                    }


                    // Change Mentioned PlayerData
                    switch (args[1].toLowerCase()) {
                        case "particle":
                            this.onSetParticle(mentioned, args[2]);
                            break;
                        case "item":
                            this.onSetItem(mentioned, args[2].toUpperCase());
                            break;
                        case "block":
                            this.onSetBlock(mentioned, args[2].toUpperCase());
                            break;
                        case "color":
                            this.onSetColor(mentioned, args[2]);
                            break;
                    }

                    messageManager.sendMessage(sender, "set-command.changed-other", StringPlaceholders.single("player", mentioned.getName()));
                }

                break;

            // If the argument was invalid, send unknown command message
            default:
                messageManager.sendMessage(sender, "unknown-command");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // If the command != Trails, return Empty List
        if (!command.getName().equalsIgnoreCase("trails"))
            return Collections.emptyList();

        // Define suggestions ArrayList;
        List<String> suggestions = new ArrayList<>();

        // If the args.length = 0 or 1
        if (args.length == 0 || args.length == 1) {
            String subCommandName = args.length == 0 ? "" : args[0];
            List<String> commands = new ArrayList<>();

            // Add reload command to TabComplete
            if (sender.hasPermission("flighttrails.reload"))
                commands.add("reload");

            // Add set command to TabComplete
            if (sender.hasPermission("flighttrails.set"))
                commands.add("set");

            // Add toggle command to TabComplete
            if (sender.hasPermission("flighttrails.toggle"))
                commands.add("toggle");


            // Add to the TabComplete
            StringUtil.copyPartialMatches(subCommandName, commands, suggestions);

        } else if (args.length == 2 && args[0].equalsIgnoreCase("set") && sender.hasPermission("flighttrails.set")) {

            List<String> options = new ArrayList<>();
            if (sender.hasPermission("flighttrails.set.particle"))
                options.add("particle");

            if (sender.hasPermission("flighttrails.set.item"))
                options.add("item");

            if (sender.hasPermission("flighttrails.set.block"))
                options.add("block");

            if (sender.hasPermission("flighttrails.set.color"))
                options.add("color");

            // Add to the TabComplete
            StringUtil.copyPartialMatches(args[1].toLowerCase(), options, suggestions);

        } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender.hasPermission("flighttrails.set")) {

            // Get the values
            String value = args[2].toLowerCase();
            String type = args[1].toLowerCase();

            switch (type) {

                case "particle":
                    List<String> particles = Arrays.stream(Particle.values())
                            .filter(particle -> sender.hasPermission("flighttrails.particle." + particle.name().toLowerCase()))
                            .map(Enum::name)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());


                    // Add to TabComplete
                    StringUtil.copyPartialMatches(value, particles, suggestions);
                    break;

                case "block":
                    // Define all the blocks into an Array
                    List<String> blocks = Arrays.stream(Material.values()).filter(Material::isBlock).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());

                    // Add to TabComplete
                    StringUtil.copyPartialMatches(value, blocks, suggestions);
                    break;

                case "item":

                    // Define all the ItemStacks into an Array
                    List<String> items = Arrays.stream(Material.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());

                    // Add to TabComplete
                    StringUtil.copyPartialMatches(value, items, suggestions);
                    break;


                case "color":
                    List<String> colors = Arrays.stream(TrailColor.values())
                            .filter(color -> sender.hasPermission("flighttrails.color." + color.name().toLowerCase()) || sender.hasPermission("flighttrails.color.*"))
                            .map(Enum::name)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());

                    // Add to tabComplete
                    StringUtil.copyPartialMatches(value, colors, suggestions);
                    break;

            }

        } else if (args.length == 4 && args[0].equalsIgnoreCase("set") && sender.hasPermission("flighttrails.set.other") || sender.hasPermission("flighttrails.toggle.other")) {
            // If the argument length is longer than 3

            // Get a new Array List
            List<String> players = new ArrayList<>();

            // Add all players that aren't vanished into ArrayList
            Bukkit.getOnlinePlayers().stream().filter(player -> !player.hasMetadata("vanished")).forEachOrdered(player -> players.add(player.getName()));

            // Add to tab complete
            StringUtil.copyPartialMatches(args[3].toLowerCase(), players, suggestions);
        }

        return suggestions;
    }

}
