package parade;

import parade.engine.GameEngine;
import parade.engine.impl.LocalGameEngine;
import parade.renderer.log.LogRenderer;
import parade.renderer.log.LogRendererProvider;
import parade.renderer.log.impl.*;
import parade.renderer.text.TextRenderer;
import parade.renderer.text.TextRendererProvider;
import parade.renderer.text.impl.BasicTextRenderer;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args)
            throws IOException, IllegalStateException, UnsupportedOperationException {

        new Settings.Builder()
                .shouldValidateProperties(true)
                .fromClasspath("config.properties")
                .build();

        LogRenderer debugRenderer = setupDebugRenderer();
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

    private static LogRenderer setupDebugRenderer() throws IOException {
        Settings settings = Settings.getInstance();

        String debugTypes = settings.get(SettingKey.CONFIG_DEBUG_TYPE);
        boolean shouldPrint = settings.getBoolean(SettingKey.CONFIG_DEBUG_ENABLED);
        LogRenderer debugRenderer;
        if (!shouldPrint) {
            debugRenderer = new NopLogRenderer();
        } else {
            String[] debugTypesArr = debugTypes.split(",");
            // Handles duplicate debug types and trim whitespace
            Set<String> debugTypesSet =
                    Stream.of(debugTypesArr).map(String::trim).collect(Collectors.toSet());
            if (debugTypesSet.size() == 1) {
                debugRenderer = new PrettyLogRenderer();
            } else {
                LogRenderer[] debugRenderers = new LogRenderer[debugTypesSet.size()];
                int i = 0;
                for (String debugType : debugTypesSet) {
                    debugRenderers[i++] = determineDebugRendererType(debugType);
                }
                debugRenderer = new MultiLogRenderer(debugRenderers);
            }
        }
        LogRendererProvider.setInstance(debugRenderer);
        debugRenderer.debug("Initialised debug renderer");

        return debugRenderer;
    }

    private static LogRenderer determineDebugRendererType(String debugType)
            throws IllegalStateException, IOException {
        return switch (debugType) {
            case "console" -> new PrettyLogRenderer();
            case "console_json" -> new JsonLogRenderer(System.out);
            case "file_json" -> {
                String filePath = Settings.getInstance().get(SettingKey.CONFIG_DEBUG_FILE);
                if (filePath == null || filePath.isEmpty()) {
                    throw new IllegalStateException(
                            "File path for debug renderer is not set in settings");
                }
                // Creates the directory if it does not exist
                Path path = Path.of(filePath);
                Path parentDir = path.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                yield new JsonLogRenderer(filePath);
            }
            default ->
                    throw new IllegalStateException("Unknown debug type in settings: " + debugType);
        };
    }

    private static TextRenderer setupTextRenderer() {
        Settings settings = Settings.getInstance();
        LogRenderer debugRenderer = LogRendererProvider.getInstance();

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
        LogRenderer debugRenderer = LogRendererProvider.getInstance();

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
