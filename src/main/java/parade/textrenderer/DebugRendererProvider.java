package parade.textrenderer;

public class DebugRendererProvider {
    private static DebugRenderer instance = null;

    public static void setInstance(DebugRenderer debugRenderer) {
        instance = debugRenderer;
    }

    /**
     * Get the instance of the DebugRenderer.
     *
     * @return the instance of the DebugRenderer that has been set to this provider
     * @throws IllegalStateException if the instance is not yet set
     */
    public static DebugRenderer getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("DebugRenderer is not yet set");
        }
        return instance;
    }
}
