package parade.menu;

public class MenuRendererProvider {
    private static MenuProvider instance = null;

    public static void setInstance(MenuProvider clientRenderer) {
        instance = clientRenderer;
    }

    /**
     * Get the instance of the ClientRenderer.
     *
     * @return the instance of the TextRenderer that has been set to this provider
     * @throws IllegalStateException if the instance is not yet set
     */
    public static MenuProvider getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("ClientRenderer is not yet set");
        }
        return instance;
    }
}
