package me.oribuin.flighttrails.menus;

import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.handlers.FlyHandler;
import me.oribuin.flighttrails.persist.ColorU;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ColorSelector implements Listener {
    private static ColorSelector INSTANCE;
    public static Inventory inv;
    public static Map<UUID, Particle.DustOptions> dustOptionsMap = new HashMap<>();

    public FlightTrails plugin;
    public FlyHandler flyHandler;
    FileConfiguration config;

    public ColorSelector(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
        config = plugin.getConfig();

        inv = Bukkit.createInventory(null, config.getInt("gui-size"), ColorU.cl(config.getString("gui-name")));
    }

    public static ColorSelector getInstance(FlightTrails plugin, FlyHandler flyHandler) {
        if (INSTANCE == null) {
            INSTANCE = new ColorSelector(plugin, flyHandler);
        }
        return INSTANCE;
    }

    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), config.getInt("particle-size"));
    }

    private void playSound(HumanEntity whoClicked) {
        Sound sound = Sound.valueOf(config.getString("click-sound"));

        Player player = (Player) whoClicked;
        player.playSound(player.getLocation(), sound, 100, 1);
    }

    Particle.DustOptions color = null;

    private void addItem(Material material, String name, int slot) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = new ArrayList<>();
        for (String s : config.getStringList("color-lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);
        inv.setItem(slot, itemStack);
    }

    public void guiItems() {
        // Red
        addItem(Material.valueOf(config.getString("red.material")), ColorU.cl(config.getString("red.name")), config.getInt("red.slot"));
        // Orange
        addItem(Material.valueOf(config.getString("orange.material")), ColorU.cl(config.getString("orange.name")), config.getInt("orange.slot"));
        // Yellow
        addItem(Material.valueOf(config.getString("yellow.material")), ColorU.cl(config.getString("yellow.name")), config.getInt("yellow.slot"));
        // Lime
        addItem(Material.valueOf(config.getString("lime.material")), ColorU.cl(config.getString("lime.name")), config.getInt("lime.slot"));
        // Green
        addItem(Material.valueOf(config.getString("green.material")), ColorU.cl(config.getString("green.name")), config.getInt("green.slot"));
        // Light Blue
        addItem(Material.valueOf(config.getString("aqua.material")), ColorU.cl(config.getString("aqua.name")), config.getInt("aqua.slot"));
        // Cyan
        addItem(Material.valueOf(config.getString("cyan.material")), ColorU.cl(config.getString("cyan.name")), config.getInt("cyan.slot"));
        // Blue
        addItem(Material.valueOf(config.getString("blue.material")), ColorU.cl(config.getString("blue.name")), config.getInt("blue.slot"));
        // Purple
        addItem(Material.valueOf(config.getString("purple.material")), ColorU.cl(config.getString("purple.name")), config.getInt("purple.slot"));
        // Pink
        addItem(Material.valueOf(config.getString("pink.material")), ColorU.cl(config.getString("pink.name")), config.getInt("pink.slot"));
        // White
        addItem(Material.valueOf(config.getString("white.material")), ColorU.cl(config.getString("white.name")), config.getInt("white.slot"));
        // Light Gray
        addItem(Material.valueOf(config.getString("light-gray.material")), ColorU.cl(config.getString("light-gray.name")), config.getInt("light-gray.slot"));
        // Gray
        addItem(Material.valueOf(config.getString("gray.material")), ColorU.cl(config.getString("gray.name")), config.getInt("gray.slot"));
        // Black
        addItem(Material.valueOf(config.getString("black.material")), ColorU.cl(config.getString("black.name")), config.getInt("black.slot"));
    }

    public void onInventory(Player player) {
        player.openInventory(inv);
        player.updateInventory();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() != inv) return;

        if (event.getClick().equals(ClickType.NUMBER_KEY)) event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);

        if (event.getClick().isLeftClick()) {
            if (clickedItem == null) return;
            if (clickedItem.getType() == Material.valueOf(config.getString("red.material"))) {

                if (player.hasPermission("flytrails.color.red")) {
                    color = particleColor(255, 0, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("orange.material"))) {

                if (player.hasPermission("flytrails.color.orange")) {
                    color = particleColor(255, 128, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("yellow.material"))) {

                if (player.hasPermission("flytrails.color.yellow")) {
                    color = particleColor(255, 255, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("lime.material"))) {

                if (player.hasPermission("flytrails.color.lime")) {
                    color = particleColor(0, 255, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("green.material"))) {

                if (player.hasPermission("flytrails.color.green")) {
                    color = particleColor(0, 128, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("aqua.material"))) {

                if (player.hasPermission("flytrails.color.lightblue")) {
                    color = particleColor(0, 255, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }
            } else if (clickedItem.getType() == Material.valueOf(config.getString("cyan.material"))) {

                if (player.hasPermission("flytrails.color.cyan")) {
                    color = particleColor(0, 102, 102);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("blue.material"))) {

                if (player.hasPermission("flytrails.color.blue")) {
                    color = particleColor(0, 0, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("purple.material"))) {

                if (player.hasPermission("flytrails.color.purple")) {
                    color = particleColor(128, 0, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("pink.material"))) {

                if (player.hasPermission("flytrails.color.pink")) {
                    color = particleColor(255, 77, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("white.material"))) {

                if (player.hasPermission("flytrails.color.white")) {
                    color = particleColor(255, 255, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("light-gray.material"))) {

                if (player.hasPermission("flytrails.color.lightgray")) {
                    color = particleColor(211, 211, 211);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("gray.material"))) {

                if (player.hasPermission("flytrails.color.gray")) {
                    color = particleColor(169, 169, 169);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.valueOf(config.getString("black.material"))) {

                if (player.hasPermission("flytrails.color.black")) {
                    color = particleColor(0, 0, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }
            }

            if (color != null)
                dustOptionsMap.put(player.getUniqueId(), color);

            if (!flyHandler.trailIsToggled(player.getUniqueId()))
                flyHandler.trailToggle(player.getUniqueId());

            if (config.getBoolean("sound-enabled", true))
                playSound(player);

            player.closeInventory();

            player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-change")));
        }
    }
}
