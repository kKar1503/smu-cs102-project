package parade;

import parade.engine.GameEngine;
import parade.engine.impl.LocalGameEngine;
import parade.logger.Logger;
import parade.logger.LoggerProvider;
import parade.logger.impl.JsonLogger;
import parade.logger.impl.MultiLogger;
import parade.logger.impl.NopLogger;
import parade.logger.impl.PrettyLogger;
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

        Logger logger = setupLogger();
        TextRenderer textRenderer = setupTextRenderer();
        GameEngine gameEngine = setupGameEngine();

        textRenderer.renderWelcome();
        Scanner scanner = new Scanner(System.in);
        logger.log("Prompting user to start game in menu");
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
                    logger.log("User is starting the game");
                    break;
                } else {
                    logger.log("User is exiting the game");
                    textRenderer.renderBye();
                    System.exit(0);
                }
            } catch (NoSuchElementException e) {
                logger.log("Invalid input received", e);
                textRenderer.renderln("Invalid input, please try again.");
            }
        }

        gameEngine.start();
    }

    private static Logger setupLogger() throws IOException {
        Settings settings = Settings.getInstance();

        String loggerTypes = settings.get(SettingKey.LOGGER_TYPES);
        boolean shouldLog = settings.getBoolean(SettingKey.LOGGER_ENABLED);
        Logger logger;
        if (!shouldLog) {
            logger = new NopLogger();
        } else {
            String[] loggerTypesArr = loggerTypes.split(",");
            // Handles duplicate logger types and trim whitespace
            Set<String> loggerTypesSet =
                    Stream.of(loggerTypesArr).map(String::trim).collect(Collectors.toSet());
            if (loggerTypesSet.size() == 1) {
                logger = new PrettyLogger();
            } else {
                Logger[] loggers = new Logger[loggerTypesSet.size()];
                int i = 0;
                for (String loggerType : loggerTypesSet) {
                    loggers[i++] = determineLoggerType(loggerType);
                }
                logger = new MultiLogger(loggers);
            }
        }
        LoggerProvider.setInstance(logger);
        logger.log("Initialised logger");

        return logger;
    }

    private static Logger determineLoggerType(String loggerType)
            throws IllegalStateException, IOException {
        return switch (loggerType) {
            case "console" -> new PrettyLogger();
            case "console_json" -> new JsonLogger(System.out);
            case "file_json" -> {
                String filePath = Settings.getInstance().get(SettingKey.LOGGER_FILE);
                if (filePath == null || filePath.isEmpty()) {
                    throw new IllegalStateException("File path for logger is not set in settings");
                }
                // Creates the directory if it does not exist
                Path path = Path.of(filePath);
                Path parentDir = path.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                yield new JsonLogger(filePath);
            }
            default ->
                    throw new IllegalStateException(
                            "Unknown logger type in settings: " + loggerType);
        };
    }

    private static TextRenderer setupTextRenderer() {
        Settings settings = Settings.getInstance();
        Logger logger = LoggerProvider.getInstance();

        String gameplayTextRenderer = settings.get(SettingKey.GAMEPLAY_TEXT_RENDERER);
        logger.log("Gameplay text renderer is using " + gameplayTextRenderer);
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
        logger.log("Initialised text renderer");

        return textRenderer;
    }

    private static GameEngine setupGameEngine() {
        Settings settings = Settings.getInstance();
        Logger logger = LoggerProvider.getInstance();

        String gameplayMode = settings.get(SettingKey.GAMEPLAY_MODE);
        logger.log("Gameplay is starting in " + gameplayMode + " mode");
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
        logger.log("Initialised game engine");

        return gameEngine;
    }
}
