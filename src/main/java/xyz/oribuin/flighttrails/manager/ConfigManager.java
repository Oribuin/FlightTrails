package xyz.oribuin.flighttrails.manager;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.flighttrails.FlightTrails;

import java.util.Arrays;
import java.util.List;

/**
 * @author Esophose
 */

public class ConfigManager extends Manager {

    public ConfigManager(FlightTrails plugin) {
        super(plugin);
        this.reload();
    }

    @Override
    public void reload() {
        // Reload config
        this.plugin.reloadConfig();

        // Save default configuration
        this.plugin.saveDefaultConfig();

        // Define the configuration
        FileConfiguration config = this.plugin.getConfig();
        for (Setting setting : Setting.values()) {
            // Set the value if it does not exist
            setting.setIfNotExists(config);

            // Load value
            setting.load(config);
        }
    }

    public enum Setting {
        CREATIVE_FLY_ENABLED("conditions.creative-fly", true),
        ELYTRA_ENABLED("conditions.elytra", true),
        DISABLED_WORLDS("disabled-worlds", Arrays.asList("disabled-world1", "disabled-world2")),
        PARTICLE_COUNT("particle-settings.count", 2),
        PARTICLE_SIZE("particle-settings.size", 1.0),
        ELYTRA_STYLE("particle-settings.elytra-style", "FANCY"),
        PARTICLE_DEFAULT_ENABLED("particle-settings.default.enabled", true),
        PARTICLE_DEFAULT_PARTICLE("particle-settings.default.particle", "REDSTONE"),
        PARTICLE_DEFAULT_BLOCK("particle-settings.default.block", "BLUE_CONCRETE"),
        PARTICLE_DEFAULT_ITEM("particle-settings.default.item", "LIGHT_BLUE_CONCRETE"),
        PARTICLE_DEFAULT_COLOR("particle-settings.default.color", "BLACK");

        private final String key;
        private final Object defaultValue;
        private Object value = null;

        Setting(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;

        }

        public void setIfNotExists(FileConfiguration config) {
            this.loadValue();

            if (this.defaultValue != null) {
                config.set(this.key, this.defaultValue);
            }

        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            this.loadValue();

            return (boolean) this.value;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            this.loadValue();

            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            this.loadValue();

            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            this.loadValue();

            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            this.loadValue();

            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            this.loadValue();

            return (String) this.value;
        }

        private double getNumber() {
            this.loadValue();

            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();

            return (List<String>) this.value;
        }

        /**
         * Loads the value from the config and caches it
         */
        private void load(FileConfiguration config) {
            this.value = config.get(this.key);
        }

        private void loadValue() {
            if (this.value != null)
                return;

            this.value = FlightTrails.getInstance().getConfig().get(this.key);
        }


    }

}
