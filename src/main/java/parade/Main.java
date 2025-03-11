package parade;

import parade.engine.GameEngine;
import parade.engine.impl.LocalGameEngine;
import parade.renderer.debug.DebugRenderer;
import parade.renderer.debug.DebugRendererProvider;
import parade.renderer.debug.impl.ConsoleDebugRenderer;
import parade.renderer.debug.impl.ConsoleJsonDebugRenderer;
import parade.renderer.debug.impl.FileJsonDebugRenderer;
import parade.renderer.debug.impl.NopDebugRenderer;
import parade.renderer.text.TextRenderer;
import parade.renderer.text.TextRendererProvider;
import parade.renderer.text.impl.BasicTextRenderer;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
            throws IOException, IllegalStateException, UnsupportedOperationException {

        new Settings.Builder()
                .shouldValidateProperties(true)
                .fromClasspath("config.properties")
                .build();

        DebugRenderer debugRenderer = setupDebugRenderer();
        TextRenderer textRenderer = setupTextRenderer();
        GameEngine gameEngine = setupGameEngine();

        textRenderer.renderWelcome();
        Scanner scanner = new Scanner(System.in);
        debugRenderer.debug("Prompting user to start game in menu");
        while (true) {
            textRenderer.renderMenu();
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input != 1 && input != 2) {
                    textRenderer.renderln("Invalid input, please only type only 1 or 2.");
                    continue;
                }

                if (input == 1) {
                    debugRenderer.debug("User is starting the game");
                    break;
                } else {
                    debugRenderer.debug("User is exiting the game");
                    textRenderer.renderBye();
                    System.exit(0);
                }
            } catch (NoSuchElementException e) {
                debugRenderer.debug("Invalid input received", e);
                textRenderer.renderln("Invalid input, please try again.");
            }
        }

        gameEngine.start();
    }

    private static DebugRenderer setupDebugRenderer() throws IOException {
        Settings settings = Settings.getInstance();

        String debugType = settings.get(SettingKey.CONFIG_DEBUG_TYPE);
        boolean shouldPrint = settings.getBoolean(SettingKey.CONFIG_DEBUG_ENABLED);
        DebugRenderer debugRenderer =
                shouldPrint
                        ? switch (debugType) {
                            case "console" -> new ConsoleDebugRenderer();
                            case "console_json" -> new ConsoleJsonDebugRenderer();
                            case "file_json" ->
                                    new FileJsonDebugRenderer(
                                            settings.get(SettingKey.CONFIG_DEBUG_FILE));
                            default ->
                                    throw new IllegalStateException(
                                            "Unknown debug type in settings: " + debugType);
                        }
                        : new NopDebugRenderer();
        DebugRendererProvider.setInstance(debugRenderer);
        debugRenderer.debug("Initialised debug renderer");

        return debugRenderer;
    }

    private static TextRenderer setupTextRenderer() {
        Settings settings = Settings.getInstance();
        DebugRenderer debugRenderer = DebugRendererProvider.getInstance();

        String gameplayTextRenderer = settings.get(SettingKey.GAMEPLAY_TEXT_RENDERER);
        debugRenderer.debug("Gameplay text renderer is using " + gameplayTextRenderer);
        TextRenderer textRenderer =
                switch (gameplayTextRenderer) {
                    case "basic" -> new BasicTextRenderer();
                    case "advanced" ->
                            throw new UnsupportedOperationException(
                                    "Advanced text renderer is not yet supported");
                    default ->
                            throw new IllegalStateException(
                                    "Unknown gameplay text renderer in settings: "
                                            + gameplayTextRenderer);
                };
        TextRendererProvider.setInstance(textRenderer);
        debugRenderer.debug("Initialised text renderer");

        return textRenderer;
    }

    private static GameEngine setupGameEngine() {
        Settings settings = Settings.getInstance();
        DebugRenderer debugRenderer = DebugRendererProvider.getInstance();

        String gameplayMode = settings.get(SettingKey.GAMEPLAY_MODE);
        debugRenderer.debug("Gameplay is starting in " + gameplayMode + " mode");
        GameEngine gameEngine =
                switch (gameplayMode) {
                    case "local" -> new LocalGameEngine();
                    case "network" ->
                            throw new UnsupportedOperationException(
                                    "Network mode is not yet supported");
                    default ->
                            throw new IllegalStateException(
                                    "Unknown gameplay mode in settings: " + gameplayMode);
                };
        debugRenderer.debug("Initialised game engine");

        return gameEngine;
    }
}
