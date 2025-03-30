package parade.settings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import parade.common.exceptions.InvalidSettingException;

class SettingsTest {
    private static Settings settings;

    @BeforeAll
    static void setup() {
        settings =
                new Settings.Builder()
                        .fromClasspath("test-config.properties") // Ensure this file is in
                        // src/test/resources
                        .shouldValidateProperties(true)
                        .build();
    }

    @Test
    void testLoadIntegerProperty() {
        int port = settings.getInt(SettingKey.SERVER_PORT);
        assertEquals(8080, port, "SERVER_PORT should be 8080");
    }

    @Test
    void testLoadStringProperty() {
        String host = settings.get(SettingKey.GAMEPLAY_MODE);
        assertEquals("local", host, "GAMEPLAY_MODE should be local");
    }

    @Test
    void testLoadBooleanProperty() {
        boolean loggerEnabled = settings.getBoolean(SettingKey.LOGGER_ENABLED);
        assertTrue(loggerEnabled, "LOGGER_ENABLED should be true");
    }

    @Test
    void testMissingPropertiesFile() {
        assertThrows(
                InvalidSettingException.class,
                () ->
                        new Settings.Builder()
                                .fromClasspath("missing-config.properties")
                                .shouldValidateProperties(true)
                                .build());
    }

    @Test
    void testMissingRequiredProperty() {
        assertThrows(
                InvalidSettingException.class,
                () ->
                        new Settings.Builder()
                                .fromClasspath("test-missing-required-config.properties")
                                .shouldValidateProperties(true)
                                .build());
    }
}
