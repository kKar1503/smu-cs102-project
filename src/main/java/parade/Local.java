package parade;

import parade.engine.LocalGameEngine;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.settings.Settings;

import java.io.IOException;

public class Local {
    public static void main(String[] args) throws IOException {

        new Settings.Builder()
                .shouldValidateProperties(true)
                .fromClasspath("config.properties")
                .build();

        LoggerProvider.setupLogger();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> LoggerProvider.getInstance().close()));
        LocalGameEngine gameEngine = setupLocalGameEngine();

        try {
            gameEngine.start();
        } catch (Exception e) {
            LoggerProvider.getInstance().log("Error occurred during game execution", e);
            System.exit(1);
        }
    }

    /**
     * Sets up the LocalGameEngine instance and configures it if required necessary.
     *
     * @return a LocalGameEngine instance
     */
    private static LocalGameEngine setupLocalGameEngine() {
        AbstractLogger logger = LoggerProvider.getInstance();

        LocalGameEngine gameEngine = new LocalGameEngine();
        logger.log("Initialised game engine");

        return gameEngine;
    }
}
