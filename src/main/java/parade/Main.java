package parade;

import parade.engine.AbstractGameEngine;
import parade.engine.impl.LocalGameEngine;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args)
            throws IOException,
                    IllegalStateException,
                    UnsupportedOperationException,
                    InterruptedException {

        new Settings.Builder()
                .shouldValidateProperties(true)
                .fromClasspath("config.properties")
                .build();

        LoggerProvider.setupLogger();
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
        AbstractLogger logger = LoggerProvider.getInstance();

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
