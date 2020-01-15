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
    // Id click off now, this is messy :sweat:

    private static ColorSelector INSTANCE;
    public static Inventory inv;
    // Create the dustOptionsMap
    public static Map<UUID, Particle.DustOptions> dustOptionsMap = new HashMap<>();
    public FlightTrails plugin;
    public FlyHandler flyHandler;
    FileConfiguration config;

    public ColorSelector(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
        config = plugin.getConfig();

        // Create the inventory
        inv = Bukkit.createInventory(null, config.getInt("gui-size"), ColorU.cl(config.getString("gui-name")));
    }

    public static ColorSelector getInstance(FlightTrails plugin, FlyHandler flyHandler) {
        // Get the ColorSelector Instance
        if (INSTANCE == null) {
            INSTANCE = new ColorSelector(plugin, flyHandler);
        }
        return INSTANCE;
    }

    // Create the particles and define the size
    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), config.getInt("particle-size"));
    }

    // Create the click sound with volume and pitch
    private void playSound(HumanEntity whoClicked) {
        Sound sound = Sound.valueOf(config.getString("click-sound"));

        Player player = (Player) whoClicked;
        player.playSound(player.getLocation(), sound, 100, 1);
    }

    // Define color
    Particle.DustOptions color = null;

    // Create the item name
    private void addItem(Material material, String name, int slot) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Create the lore array list
        List<String> lore = new ArrayList<>();
        for (String s : config.getStringList("color-lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        // Set the itemMeta Lore & Name
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);
        }

        // Apply the meta onto the item and set the item slot
        itemStack.setItemMeta(itemMeta);
        inv.setItem(slot, itemStack);
    }

    // Define all the items in the GUI   with the name, material and slot.
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

    // Update Inventory & Open the inventory
    public void onInventory(Player player) {
        player.updateInventory();
        player.openInventory(inv);
    }

    // The messiest part
    // Start the click event
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String noPerm = ColorU.cl(config.getString("prefix") + config.getString("color-permission"));

        // If an inventory being clicked on is not the gui return
        if (event.getView().getTopInventory() != inv) return;

        // If a user tries to hotkey numbers cancel click event
        if (event.getClick().equals(ClickType.NUMBER_KEY)) event.setCancelled(true);

        // Get the player & clicked Item.
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Material itemType = event.getCurrentItem().getType();
        // Cancel click event entirely
        event.setCancelled(true);

        // If click is a left click:
        if (event.getClick().isLeftClick()) {
            // If the clicked item is null do nothing
            if (clickedItem == null) return;

            /*
            Same thing on repeat to be honest.

            If the clicked item is COLOR.material do the following

            if player does not have permission flytrails.color.COLOR
            Send the noPerm messages
            only, Else Set the particle color
             */
            if (itemType == Material.valueOf(config.getString("red.material"))) {
                /*
                Same thing on repeat really,
                 */
                if (!player.hasPermission("flytrails.color.red")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(255, 0, 0);
            }

            if (itemType == Material.valueOf(config.getString("orange.material"))) {
                if (!player.hasPermission("flytrails.color.orange")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(255, 128, 0);
            }

            if (itemType == Material.valueOf(config.getString("yellow.material"))) {

                if (!player.hasPermission("flytrails.color.yellow")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(255, 255, 0);
            }

            if (itemType == Material.valueOf(config.getString("lime.material"))) {

                if (!player.hasPermission("flytrails.color.lime")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(0, 255, 0);
            }

            if (itemType == Material.valueOf(config.getString("green.material"))) {

                if (!player.hasPermission("flytrails.color.green")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(0, 128, 0);
            }

            if (itemType == Material.valueOf(config.getString("aqua.material"))) {

                if (!player.hasPermission("flytrails.color.lightblue")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(0, 255, 255);
            }

            if (itemType == Material.valueOf(config.getString("cyan.material"))) {

                if (!player.hasPermission("flytrails.color.cyan")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(0, 102, 102);
            }

            if (itemType == Material.valueOf(config.getString("blue.material"))) {

                if (!player.hasPermission("flytrails.color.blue")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(0, 0, 255);
            }

            if (itemType == Material.valueOf(config.getString("purple.material"))) {

                if (!player.hasPermission("flytrails.color.purple")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(128, 0, 255);
            }

            if (itemType == Material.valueOf(config.getString("pink.material"))) {

                if (!player.hasPermission("flytrails.color.pink")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(255, 77, 255);
            }

            if (itemType == Material.valueOf(config.getString("white.material"))) {

                if (!player.hasPermission("flytrails.color.white")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(255, 255, 255);
            }

            if (itemType == Material.valueOf(config.getString("light-gray.material"))) {

                if (!player.hasPermission("flytrails.color.lightgray")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(211, 211, 211);
            }

            if (itemType == Material.valueOf(config.getString("gray.material"))) {

                if (!player.hasPermission("flytrails.color.gray")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(169, 169, 169);
            }

            if (itemType == Material.valueOf(config.getString("black.material"))) {
                if (!player.hasPermission("flytrails.color.black")) {
                    player.sendMessage(noPerm);
                    return;
                }
                color = particleColor(0, 0, 0);
            }

            // If color isn't null, put the player in the dustOptionsMap.
            if (color != null)
                dustOptionsMap.put(player.getUniqueId(), color);

            // If the toggle isn't enabled when you click on an item, Enable it.
            if (!flyHandler.trailIsToggled(player.getUniqueId()))
                flyHandler.trailToggle(player.getUniqueId());

            // If the option for sound is enabled, play the sound.
            if (config.getBoolean("sound-enabled", true))
                playSound(player);

            // Close the inventory on item click
            player.closeInventory();

            // If the message is enabled.
            if (config.getBoolean("gui-color-message", true)) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("color-change")));
            }
        }
    }
}
