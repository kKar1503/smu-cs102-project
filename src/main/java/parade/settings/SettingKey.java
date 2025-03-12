package parade.settings;

public enum SettingKey {
    LOGGER_ENABLED("logger.enabled", false),
    LOGGER_TYPES("logger.types", false),
    LOGGER_FILE("logger.file", false),
    SERVER_PORT("server.port", true),
    SERVER_HOST("server.host", true),
    SERVER_THREADS("server.threads", true),
    SERVER_TIMEOUT("server.timeout", true),
    CLIENT_RENDERER("client.renderer", false),
    GAMEPLAY_MODE("gameplay.mode", true);

    private final String key;
    private final boolean required;

    SettingKey(String key, boolean required) {
        this.key = key;
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return required;
    }
}
