package parade.settings;

import parade.exceptions.InvalidSettingException;

import java.io.*;
import java.util.Properties;

/**
 * This is a singleton class that holds the settings for the application.
 *
 * <p>The settings are loaded from a properties file. The file can be loaded from the classpath or
 * from an external file.
 *
 * <p>The Settings class should be initialised using the Builder subclass.
 */
public class Settings {
    private static final String DEFAULT_FILE_PATH = "config/config.properties";
    private static final String CONFIG_FILE_PATH_ENV = "CONFIG_PATH";

    private static Settings instance = null;
    private final Properties properties = new Properties();

    /**
     * Private constructor to create the Settings instance.
     *
     * @throws InvalidSettingException if the properties are not found or if there is an error
     *     reading the file
     */
    private Settings() throws InvalidSettingException {
        String filePath = System.getenv(CONFIG_FILE_PATH_ENV);
        if (filePath == null || filePath.isEmpty()) {
            filePath = DEFAULT_FILE_PATH;
        }
        loadProperties(filePath);
        validateRequiredProperties();
    }

    /**
     * Retrieve the instance of the Settings class.
     *
     * @return the instance of the Settings class
     * @throws InvalidSettingException if the Settings class is not initialized
     */
    public static Settings get() throws InvalidSettingException {
        if (instance == null) {
            return instance = new Settings();
        }
        return instance;
    }

    /**
     * @param filePath the file path of the properties file
     * @throws InvalidSettingException if the file is not found or if there is an error reading the
     *     file
     */
    private void loadProperties(String filePath) throws InvalidSettingException {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new InvalidSettingException("Failed to load configuration: " + filePath, e);
        }
    }

    /**
     * This method validates that all the required properties are present in the properties file.
     *
     * @throws InvalidSettingException if a required property is not found
     */
    private void validateRequiredProperties() throws InvalidSettingException {
        for (SettingKey key : SettingKey.values()) {
            if (key.isRequired() && properties.getProperty(key.getKey()) == null) {
                throw new InvalidSettingException("Required property not found: " + key.getKey());
            }
        }
    }

    /**
     * Get the value of the property. The method returns null if the property is not found.
     *
     * @param key the key of the property
     * @return the value of the property
     */
    public String get(SettingKey key) {
        return properties.getProperty(key.getKey());
    }

    /**
     * Get the integer value of the property.
     *
     * @param key the key of the property
     * @return the integer value of the property
     * @throws NumberFormatException if the value is not able to parse into an integer
     */
    public int getInt(SettingKey key) throws NumberFormatException {
        return Integer.parseInt(properties.getProperty(key.getKey()));
    }

    /**
     * Get the boolean value of the property. If the value is not a boolean, it will default to
     * false.
     *
     * @param key the key of the property
     * @return the boolean value of the property
     */
    public boolean getBoolean(SettingKey key) {
        return Boolean.parseBoolean(properties.getProperty(key.getKey()));
    }
}
