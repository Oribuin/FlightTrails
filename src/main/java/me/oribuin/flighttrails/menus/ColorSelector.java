package me.oribuin.flighttrails.menus;

import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.handlers.FlyHandler;
import me.oribuin.flighttrails.persist.ColorU;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ColorSelector implements Listener {
    private static ColorSelector INSTANCE;
    private Inventory inv;
    public static Map<UUID, Particle.DustOptions> dustOptionsMap = new HashMap<>();

    FlyHandler flyHandler;
    FlightTrails plugin;
    FileConfiguration config;

    public ColorSelector(FlyHandler flyHandler, FlightTrails instance) {
        this.flyHandler = flyHandler;
        this.plugin = instance;
        config = plugin.getConfig();
    }

    public static ColorSelector getInstance(FlyHandler flyHandler, FlightTrails instance) {
        if (INSTANCE == null) {
            INSTANCE = new ColorSelector(flyHandler, instance);
        }
        return INSTANCE;
    }


    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), 2);
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

        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            itemMeta.setLore(Arrays.asList("",
                    ColorU.cl("&bClick&f to enable Color")));
        }

        itemStack.setItemMeta(itemMeta);
        inv.setItem(slot, itemStack);

    }


    public void guiItems() {
        // Toggle Fly
        addItem(Material.NAME_TAG, ColorU.cl("&eToggle Trail"), 4);
        // Red
        addItem(Material.RED_DYE, ColorU.cl("&CRed"), 10);
        // Orange
        addItem(Material.ORANGE_DYE, ColorU.cl("&6Orange"), 11);
        // Yellow
        addItem(Material.YELLOW_DYE, ColorU.cl("&eYellow"), 12);
        // Lime
        addItem(Material.LIME_DYE, ColorU.cl("&aLime"), 13);
        // Green
        addItem(Material.GREEN_DYE, ColorU.cl("&2Green"), 14);
        // Light Blue
        addItem(Material.LIGHT_BLUE_DYE, ColorU.cl("&aAqua"), 15);
        // Cyan
        addItem(Material.CYAN_DYE, ColorU.cl("&3Cyan"), 16);
        // Blue
        addItem(Material.BLUE_DYE, ColorU.cl("&9Blue"), 19);
        // Purple
        addItem(Material.PURPLE_DYE, ColorU.cl("&5Purple"), 20);
        // Pink
        addItem(Material.PINK_DYE, ColorU.cl("&dPink"), 21);
        // White
        addItem(Material.WHITE_DYE, ColorU.cl("&rWhite"), 22);
        // Light Gray
        addItem(Material.LIGHT_GRAY_DYE, ColorU.cl("&7Light Gray"), 23);
        // Gray
        addItem(Material.GRAY_DYE, ColorU.cl("&8Gray"), 24);
        // Black
        addItem(Material.BLACK_DYE, ColorU.cl("&8&lBlack"), 25);
    }

    public void onInventory(Player player) {
        player.openInventory(inv);
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

            if (clickedItem.getType() == Material.NAME_TAG) {
                player.performCommand("flytrail");
                player.closeInventory();
                playSound(player);

                return;
            } else if (clickedItem.getType() == Material.RED_DYE) {

                if (player.hasPermission("flytrails.color.red")) {
                    color = particleColor(255, 0, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.ORANGE_DYE) {

                if (player.hasPermission("flytrails.color.orange")) {
                    color = particleColor(255, 128, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.YELLOW_DYE) {

                if (player.hasPermission("flytrails.color.yellow")) {
                    color = particleColor(255, 255, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.LIME_DYE) {

                if (player.hasPermission("flytrails.color.lime")) {
                    color = particleColor(0, 255, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.GREEN_DYE) {

                if (player.hasPermission("flytrails.color.green")) {
                    color = particleColor(0, 128, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.LIGHT_BLUE_DYE) {

                if (player.hasPermission("flytrails.color.lightblue")) {
                    color = particleColor(0, 255, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }
            } else if (clickedItem.getType() == Material.CYAN_DYE) {

                if (player.hasPermission("flytrails.color.cyan")) {
                    color = particleColor(0, 102, 102);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.BLUE_DYE) {

                if (player.hasPermission("flytrails.color.blue")) {
                    color = particleColor(0, 0, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.PURPLE_DYE) {

                if (player.hasPermission("flytrails.color.purple")) {
                    color = particleColor(128, 0, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.PINK_DYE) {

                if (player.hasPermission("flytrails.color.pink")) {
                    color = particleColor(255, 77, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.WHITE_DYE) {

                if (player.hasPermission("flytrails.color.white")) {
                    color = particleColor(255, 255, 255);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.LIGHT_GRAY_DYE) {

                if (player.hasPermission("flytrails.color.lightgray")) {
                    color = particleColor(211, 211, 211);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.GRAY_DYE) {

                if (player.hasPermission("flytrails.color.gray")) {
                    color = particleColor(169, 169, 169);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }

            } else if (clickedItem.getType() == Material.BLACK_DYE) {

                if (player.hasPermission("flytrails.color.black")) {
                    color = particleColor(0, 0, 0);
                } else {
                    player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-permission")));
                }
            }

            if (color != null) {
                dustOptionsMap.put(player.getUniqueId(), color);
            }

            if (config.getBoolean("sound-enabled")) {
                playSound(player);
            }

            player.sendMessage(ColorU.cl(config.getString("prefix" + config.getString("color-change"))));
        }
    }
}
