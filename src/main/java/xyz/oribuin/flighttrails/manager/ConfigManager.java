package xyz.oribuin.flighttrails.manager;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.flighttrails.FlightTrails;

public class ConfigManager extends Manager {

    public ConfigManager(FlightTrails plugin) {
        super(plugin);
        this.reload();
    }

    public enum Setting {
        CREATIVE_FLY_ENABLED("conditions.creative-fly"),
        ELYTRA_ENABLED("conditions.elytra"),
        DISABLED_WORLDS("disabled-worlds"),
        PARTICLE_COUNT("particle-settings.count"),
        PARTICLE_SIZE("particle-settings.size"),
        PARTICLE_DEFAULT_ENABLED("particle-settings.default.enabled"),
        PARTICLE_DEFAULT_PARTICLE("particle-settings.default.particle"),
        PARTICLE_DEFAULT_BLOCK("particle-settings.default.block"),
        PARTICLE_DEFAULT_ITEM("particle-settings.default.item"),
        PARTICLE_DEFAULT_COLOR("particle-settings.default.color"),
        FILE_VERSION("file-version");

        private final String key;
        private Object value = null;

        Setting(String key) {
            this.key = key;
        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            return (boolean) this.value;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            return (String) this.value;
        }

        private double getNumber() {
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
            return (List<String>) this.value;
        }

        /**
         * Loads the value from the config and caches it
         */
        private void load(FileConfiguration config) {
            this.value = config.get(this.key);
        }

    }

    @Override
    public void reload() {
        this.plugin.reloadConfig();
        this.plugin.saveDefaultConfig();

        FileConfiguration config = this.plugin.getConfig();
        for (Setting value : Setting.values())
            value.load(config);
    }

}
