package parade.textrenderer;

public class TextRendererProvider {
    private static TextRenderer instance = null;

    public static void setInstance(TextRenderer textRenderer) {
        instance = textRenderer;
    }

    /**
     * Get the instance of the TextRenderer.
     *
     * @return the instance of the TextRenderer that has been set to this provider
     * @throws IllegalStateException if the instance is not yet set
     */
    public static TextRenderer getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("TextRenderer is not yet set");
        }
        return instance;
    }
}
