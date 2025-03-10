package parade.textrenderer;

public interface DebugRenderer {
    public void debug(String message);

    public void debug(String message, Exception t);

    public void debugf(String format, Object... args);
}
