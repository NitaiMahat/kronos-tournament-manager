package edu.augustana.csc305.project.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Utility class responsible for reading and managing application configuration properties.
 *
 * <p>It attempts to load configuration from sources in a defined priority order:</p>
 * <ol>
 * <li>The {@code .env} file located in the project's root directory (intended for local development).</li>
 * <li>The {@code config.properties} file located in the application's classpath (resources).</li>
 * <li>Hardcoded default values as a final fallback.</li>
 * </ol>
 *
 * @author Java doc by Gemini 2.5 Flash
 */
public class ApplicationConfig {

    private static final Properties PROPERTIES = new Properties();

    static {
        boolean loaded = false;

        try (InputStream envInput = Files.newInputStream(Paths.get(".env"))) {
            PROPERTIES.load(envInput);
            System.out.println("Configuration loaded from .env file.");
            loaded = true;
        } catch (IOException ignored) {
        }

        if (!loaded) {
            try (InputStream resourceInput = ApplicationConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (resourceInput != null) {
                    PROPERTIES.load(resourceInput);
                    System.out.println("Configuration loaded from config.properties.");
                    loaded = true;
                }
            } catch (IOException ignored) {
            }
        }

        if (!PROPERTIES.containsKey("API_BASE_URL")) {
            System.err.println("WARNING: API_BASE_URL not found in configuration. Using default: http://localhost:7000/");
            PROPERTIES.setProperty("API_BASE_URL", "http://localhost:7000/");
        }
    }

    /**
     * Retrieves the configured API base URL.
     *
     * @return The configured base URL as a String.
     */
    public static String getApiBaseUrl() {
        return PROPERTIES.getProperty("API_BASE_URL");
    }

    /**
     * Retrieves the value of a specific property key from the loaded configuration.
     *
     * @param key The property key to look up.
     * @return The string value of the property, or {@code null} if the key is not found.
     */
    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}