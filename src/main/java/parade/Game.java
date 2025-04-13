package parade;

import parade.core.GameEngine;
import parade.logger.LoggerProvider;

import java.io.IOException;

public class Game {
    public static void main(String[] args) throws IOException {
        LoggerProvider.setupLogger();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> LoggerProvider.getInstance().close()));
        try {
            new GameEngine().start();
        } catch (Exception e) {
            LoggerProvider.getInstance().log("Error occurred during game execution", e);
            System.exit(1);
        }
    }
}
