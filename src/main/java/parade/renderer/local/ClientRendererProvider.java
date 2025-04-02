package parade.renderer.local;

public class ClientRendererProvider {
    private static IClientRenderer instance = null;

    public static void setInstance(IClientRenderer clientRenderer) {
        instance = clientRenderer;
    }

    /**
     * Get the instance of the ClientRenderer.
     *
     * @return the instance of the TextRenderer that has been set to this provider
     * @throws IllegalStateException if the instance is not yet set
     */
    public static IClientRenderer getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("ClientRenderer is not yet set");
        }
        return instance;
    }
}
