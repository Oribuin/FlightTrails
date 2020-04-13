package xyz.oribuin.flighttrails.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.bukkit.plugin.Plugin;

public final class FileUtils {

    /**
     * Creates a file on disk from a file located in the jar
     *
     * @param fileName The name of the file to create
     */
    public static void createFile(Plugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try (InputStream inStream = plugin.getResource(fileName)) {
                if (inStream == null) {
                    file.createNewFile();
                    return;
                }

                Files.copy(inStream, Paths.get(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
