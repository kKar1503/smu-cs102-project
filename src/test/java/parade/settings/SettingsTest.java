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
        assertEquals(8080, port, "Server port should be 8080");
    }

    @Test
    void testLoadStringProperty() {
        String host = settings.get(SettingKey.SERVER_HOST);
        assertEquals("127.0.0.1", host, "Server host should be 127.0.0.1");
    }

    @Test
    void testLoadBooleanProperty() {
        boolean debug = settings.getBoolean(SettingKey.CONFIG_DEBUG_ENABLED);
        assertTrue(debug, "Config debug should be true");
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
