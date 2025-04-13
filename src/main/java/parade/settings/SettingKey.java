package parade.settings;

public enum SettingKey {
    LOGGER_ENABLED("logger.enabled", false),
    LOGGER_TYPES("logger.types", false),
    LOGGER_FILE("logger.file", false),
    CLIENT_MENU("client.menu", false);

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
