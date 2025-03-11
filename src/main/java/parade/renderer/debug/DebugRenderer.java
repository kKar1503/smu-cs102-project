package parade.renderer.debug;

public interface DebugRenderer {
    void debug(String message);

    void debug(String message, Exception t);

    void debugf(String format, Object... args);
}
