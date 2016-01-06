package fr.ktoublanc.config.management;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by kevin on 04/01/2016.
 */
public class ConfigReader {

    private final String resourceName;
    private final Properties properties = new Properties();

    public ConfigReader(@NotNull final String resourceName) throws IOException {
        Objects.requireNonNull(resourceName, "Can not load configuration for null resource name");

        this.resourceName = resourceName;
        this.loadProperties();
    }

    private void loadProperties() throws IOException {
        try {
            // First reading from file
            final URL resourceURL = this.getClass().getClassLoader().getResource(resourceName);
            final URL resourceDirectoryURL = this.getClass().getClassLoader().getResource(resourceName + ".d");
            if (resourceURL == null && resourceDirectoryURL == null) {
                throw new IOException("Unable to find configuration files for resource name: " + resourceName);
            }

            if (resourceURL != null) {
                final Path path = Paths.get(resourceURL.toURI());
                this.loadFile(path);
            }

            // Then reading from directory if found
            if (resourceDirectoryURL != null) {
                final Path path = Paths.get(resourceDirectoryURL.toURI());
                this.loadFiles(path);
            }

        } catch (URISyntaxException e) {
            throw new IOException("Error while reading configuration: " + resourceName, e);
        }
    }

    private void loadFiles(@NotNull final Path directoryPath) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path path : stream) {
                final String fileName = path.getFileName().toString();
                if (!fileName.matches("^\\d+_.*")) {
                    continue;
                }
                this.loadFile(path);
            }
        }
    }

    private void loadFile(@NotNull final Path path) throws IOException {
        try (final InputStream stream = Files.newInputStream(path)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new IOException("Unable to lead configuration file: " + path.getFileName());
        }
    }

    public String retrieve(@NotNull final String key) {
        return properties.getProperty(key);
    }
}
