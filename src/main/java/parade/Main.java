package parade;

import parade.engine.AbstractGameEngine;
import parade.engine.impl.LocalGameEngine;
import parade.logger.Logger;
import parade.logger.LoggerProvider;
import parade.logger.impl.JsonLogger;
import parade.logger.impl.MultiLogger;
import parade.logger.impl.NopLogger;
import parade.logger.impl.PrettyLogger;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args)
            throws IOException, IllegalStateException, UnsupportedOperationException {

        new Settings.Builder()
                .shouldValidateProperties(true)
                .fromClasspath("config.properties")
                .build();

        setupLogger();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> LoggerProvider.getInstance().close()));
        AbstractGameEngine gameEngine = setupGameEngine();

        try {
            gameEngine.start();
        } catch (Exception e) {
            LoggerProvider.getInstance().log("Error occurred during game execution", e);
            System.exit(1);
        }
    }

    private static void setupLogger() throws IOException {
        Settings settings = Settings.getInstance();

        String loggerTypes = settings.get(SettingKey.LOGGER_TYPES);
        boolean shouldLog = settings.getBoolean(SettingKey.LOGGER_ENABLED);
        Logger logger;
        if (!shouldLog) {
            logger = new NopLogger();
        } else {
            String[] loggerTypesArr = loggerTypes.split(",");
            // Handles duplicate logger types and trim whitespace
            List<String> loggerTypesArrNoDuplicates =
                    Stream.of(loggerTypesArr).map(String::trim).distinct().toList();
            if (loggerTypesArrNoDuplicates.size() == 1) {
                logger = determineLoggerType(loggerTypesArrNoDuplicates.getFirst());
            } else {
                Logger[] loggers = new Logger[loggerTypesArrNoDuplicates.size()];
                int i = 0;
                for (String loggerType : loggerTypesArrNoDuplicates) {
                    loggers[i++] = determineLoggerType(loggerType);
                }
                logger = new MultiLogger(loggers);
            }
        }
        LoggerProvider.setInstance(logger);
        logger.log("Initialised logger");
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

    /**
     * Sets up the game engine based on the gameplay mode specified in the settings.
     *
     * @return the game engine
     * @throws IllegalStateException if the gameplay mode is not set or wrongly set in the settings
     * @throws UnsupportedOperationException if the gameplay mode is not supported
     */
    private static AbstractGameEngine setupGameEngine()
            throws IllegalStateException, UnsupportedOperationException {
        Settings settings = Settings.getInstance();
        Logger logger = LoggerProvider.getInstance();

        String gameplayMode = settings.get(SettingKey.GAMEPLAY_MODE);
        logger.log("Gameplay is starting in " + gameplayMode + " mode");
        AbstractGameEngine gameEngine =
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
