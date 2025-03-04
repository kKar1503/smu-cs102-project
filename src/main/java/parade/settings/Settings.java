package parade.settings;

import parade.common.exceptions.InvalidSettingException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    static Settings instance = null;
    private final Properties properties = new Properties();

    /**
     * Private constructor to create the Settings instance.
     *
     * @param filePath the file path of the properties file
     * @param isClasspath if the given file path is in the classpath or external file
     * @param shouldValidateProperties if the properties should be validated
     * @throws InvalidSettingException if the properties are not found or if there is an error
     *     reading the file
     */
    private Settings(String filePath, boolean isClasspath, boolean shouldValidateProperties)
            throws InvalidSettingException {
        loadProperties(filePath, isClasspath);

        if (shouldValidateProperties) {
            validateRequiredProperties();
        }
    }

    /**
     * Retrieve the instance of the Settings class.
     *
     * @return the instance of the Settings class
     * @throws InvalidSettingException if the Settings class is not initialized
     */
    public static Settings getInstance() throws InvalidSettingException {
        if (instance == null) {
            throw new InvalidSettingException("Settings not initialized. Use Settings.Builder.");
        }
        return instance;
    }

    /**
     * Load the properties from the file.
     *
     * @param filePath the file path of the properties file
     * @param isClasspath true if the file is in the classpath, false if it is an external file,
     *     this is used to determined where the properties are being read from.
     * @throws InvalidSettingException if the file is not found or if there is an error reading the
     *     file
     */
    private void loadProperties(String filePath, boolean isClasspath)
            throws InvalidSettingException {
        try (InputStream input =
                isClasspath
                        ? getClass().getClassLoader().getResourceAsStream(filePath)
                        : new FileInputStream(filePath)) {

            if (input == null) {
                throw new IOException("Configuration file not found: " + filePath);
            }
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

    /**
     * Builder class to create the Settings instance.
     *
     * <p>The Builder class provides two methods to specify the file path: fromClasspath() and
     * fromExternalFile(). The build() method must be called to create the Settings instance.
     */
    public static class Builder {
        private String filePath = "config.properties"; // Default: config.properties
        private boolean isClasspath = true; // Default: Load from classpath
        private boolean shouldValidateProperties = true; // Default: Validate properties

        public Builder fromClasspath(String filePath) {
            this.filePath = filePath;
            this.isClasspath = true;
            return this;
        }

        public Builder fromExternalFile(String filePath) {
            this.filePath = filePath;
            this.isClasspath = false;
            return this;
        }

        public Builder shouldValidateProperties(boolean shouldValidate) {
            this.shouldValidateProperties = shouldValidate;
            return this;
        }

        /**
         * Build the Settings instance.
         *
         * @return the Settings instance
         * @throws InvalidSettingException if the properties are not found or if there is an error
         *     reading the file
         */
        public Settings build() throws InvalidSettingException {
            // If not specified, we will use the default values
            instance = new Settings(filePath, isClasspath, shouldValidateProperties);
            return instance;
        }
    }
}
