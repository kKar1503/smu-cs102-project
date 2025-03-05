package parade.textrenderer;

public interface DebugRenderer {
    public void debug(String message);

    public void debug(String message, Throwable t);

    public void debug(String format, Object... args);

    public void debugf(String format, Object... args);
}
