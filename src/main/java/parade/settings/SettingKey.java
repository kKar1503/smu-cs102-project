package parade.settings;

public enum SettingKey {
    LOGGER_ENABLED("logger.enabled", false),
    LOGGER_TYPES("logger.types", false),
    LOGGER_FILE("logger.file", false),
    SERVER_PORT("server.port", true),
    SERVER_HOST("server.host", true),
    SERVER_THREADS("server.threads", true),
    SERVER_TIMEOUT("server.timeout", true),
    GAMEPLAY_MODE("gameplay.mode", true),
    GAMEPLAY_TEXT_RENDERER("gameplay.textrenderer", false);

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
