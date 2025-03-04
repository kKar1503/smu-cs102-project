package parade.settings;

public enum SettingKey {
    CONFIG_DEBUG("config.debug", false),
    SERVER_PORT("server.port", true),
    SERVER_HOST("server.host", true),
    SERVER_THREADS("server.threads", true),
    SERVER_TIMEOUT("server.timeout", true),
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
