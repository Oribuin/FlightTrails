package xyz.oribuin.flighttrails.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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

public class CommandManager extends Manager implements TabExecutor {

    public CommandManager(FlightTrails plugin) {
        super(plugin);

        PluginCommand trailsCommand = this.plugin.getCommand("trails");
        if (trailsCommand != null) {
            trailsCommand.setExecutor(this);
            trailsCommand.setTabCompleter(this);
        }
    }

    @Override
    public void reload() {
        // Unused
    }

    public void onReloadCommand(CommandSender sender) {
        MessageManager messageManager = this.plugin.getMessageManager();
        if (!sender.hasPermission("flighttrails.reload")) {
            messageManager.sendMessage(sender, "invalid-permission");
            return;
        }

        this.plugin.reload();
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.getDescription().getVersion()));
    }

    public void onToggleCommand(Player player, boolean isNew) {
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();
        if (isNew) {
            dataManager.getPlayerData(player, true);
            messageManager.sendMessage(player, "trails-enabled");
            return;
        }

        PlayerData playerData = dataManager.getPlayerData(player, false);
        boolean enabled = !playerData.isEnabled();
        dataManager.updatePlayerData(player, enabled);
        if (enabled) {
            messageManager.sendMessage(player, "trails-enabled");
        } else {
            messageManager.sendMessage(player, "trails-disabled");
        }
    }

    public void onSetParticle(Player player, String particleValue) {
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        if (!player.hasPermission("flighttrails.set.particle")) {
            messageManager.sendMessage(player, "invalid-permission");
            return;
        }

        Particle particle;
        try {
            particle = Particle.valueOf(particleValue.toUpperCase());
        } catch (Exception ex) {
            messageManager.sendMessage(player, "set-command.invalid-particle");
            return;
        }

        dataManager.updatePlayerData(player, particle);
        messageManager.sendMessage(player, "set-command.particle", StringPlaceholders.single("particle", particle.name().toLowerCase()));
    }

    public void onSetItem(Player player, String itemValue) {
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        if (!player.hasPermission("flighttrails.set.item")) {
            messageManager.sendMessage(player, "set-command.invalid-permission");
            return;
        }

        PlayerData playerData = dataManager.getPlayerData(player, true);
        Particle particle = playerData.getParticle();
        if (particle != Particle.ITEM_CRACK) {
            messageManager.sendMessage(player, "set-command.required-particle");
            return;
        }

        Material material = Material.getMaterial(itemValue);
        if (material == null) {
            messageManager.sendMessage(player, "set-command.invalid-item");
            return;
        }

        ItemStack item = new ItemStack(material);
        dataManager.updatePlayerData(player, item);
        messageManager.sendMessage(player, "set-command.item", StringPlaceholders.single("item", material.name().toLowerCase()));
    }

    public void onSetBlock(Player player, String blockValue) {
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        if (!player.hasPermission("flighttrails.set.block")) {
            messageManager.sendMessage(player, "set-command.invalid-permission");
            return;
        }

        PlayerData playerData = dataManager.getPlayerData(player, true);
        Particle particle = playerData.getParticle();
        if (particle != Particle.BLOCK_CRACK && particle != Particle.BLOCK_DUST && particle != Particle.FALLING_DUST) {
            messageManager.sendMessage(player, "set-command.required-particle");
            return;
        }

        Material material = Material.getMaterial(blockValue);
        if (material == null) {
            messageManager.sendMessage(player, "set-command.invalid-block");
            return;
        }

        BlockData block = material.createBlockData();
        dataManager.updatePlayerData(player, block);
        messageManager.sendMessage(player, "set-command.block", StringPlaceholders.single("block", material.name().toLowerCase()));
    }

    public void onSetColor(Player player, String colorValue) {
        MessageManager messageManager = this.plugin.getMessageManager();
        DataManager dataManager = this.plugin.getDataManager();

        if (!player.hasPermission("flighttrails.set.color")) {
            messageManager.sendMessage(player, "set-command.invalid-permission");
            return;
        }

        PlayerData playerData = dataManager.getPlayerData(player, true);
        Particle particle = playerData.getParticle();
        if (particle != Particle.REDSTONE) {
            messageManager.sendMessage(player, "set-command.required-particle");
            return;
        }

        TrailColor color;
        try {
            color = TrailColor.valueOf(colorValue.toUpperCase());
        } catch (Exception ex) {
            messageManager.sendMessage(player, "set-command.invalid-color");
            return;
        }

        dataManager.updatePlayerData(player, color);
        messageManager.sendMessage(player, "set-command.color", StringPlaceholders.single("color", color.name().toLowerCase()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("trails"))
            return true;

        MessageManager messageManager = this.plugin.getMessageManager();
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                messageManager.sendMessage(sender, "player-only");
                return true;
            }

            if (!sender.hasPermission("flighttrails.toggle")) {
                messageManager.sendMessage(sender, "invalid-permission");
                return true;
            }

            Player player = (Player) sender;
            this.onToggleCommand(player, this.plugin.getDataManager().getPlayerData(player, false) == null);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                this.onReloadCommand(sender);
                break;
            case "set":
                if (!(sender instanceof Player)) {
                    messageManager.sendMessage(sender, "player-only");
                    break;
                }

                if (args.length == 1) {
                    messageManager.sendMessage(sender, "no-type");
                    break;
                } else if (args.length == 2) {
                    messageManager.sendMessage(sender, "no-value");
                    break;
                }

                Player player = (Player) sender;
                switch (args[1].toLowerCase()) {
                    case "particle":
                        this.onSetParticle(player, args[2]);
                        break;
                    case "item":
                        this.onSetItem(player, args[2]);
                        break;
                    case "block":
                        this.onSetBlock(player, args[2]);
                        break;
                    case "color":
                        this.onSetColor(player, args[2]);
                        break;
                }
                break;
            default:
                messageManager.sendMessage(sender, "unknown-command");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("trails"))
            return Collections.emptyList();

        List<String> suggestions = new ArrayList<>();
        if (args.length == 0 || args.length == 1) {
            String subCommandName = args.length == 0 ? "" : args[0];
            List<String> commands = new ArrayList<>();
            if (sender.hasPermission("flighttrails.reload"))
                commands.add("reload");
            commands.add("set");
            StringUtil.copyPartialMatches(subCommandName, commands, suggestions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            List<String> options = Arrays.asList("particle", "item", "block", "color");
            StringUtil.copyPartialMatches(args[1].toLowerCase(), options, suggestions);
        } else if (args.length > 2 && args[0].equalsIgnoreCase("set")) {
            String value = args[2].toLowerCase();
            String type = args[1].toLowerCase();
            switch (type) {
                case "particle":
                    List<String> particles = Arrays.stream(Particle.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                    StringUtil.copyPartialMatches(value, particles, suggestions);
                    break;
                case "block":
                    List<String> blocks = Arrays.stream(Material.values()).filter(Material::isBlock).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                    StringUtil.copyPartialMatches(value, blocks, suggestions);
                    break;
                case "item":
                    List<String> items = Arrays.stream(Material.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                    StringUtil.copyPartialMatches(value, items, suggestions);
                    break;
                case "color":
                    List<String> colors = Arrays.stream(TrailColor.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                    StringUtil.copyPartialMatches(value, colors, suggestions);
                    break;
            }
        }

        return suggestions;
    }

}
