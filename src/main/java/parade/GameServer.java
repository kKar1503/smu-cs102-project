package parade;

import parade.logger.LoggerProvider;
import parade.network.server.Server;
import parade.settings.Settings;

import java.io.IOException;

public class GameServer {
    public static void main(String[] args) throws IOException {
        new Settings.Builder()
                .shouldValidateProperties(true)
                .fromClasspath("config.properties")
                .build();
        LoggerProvider.setupLogger();

        try (Server gameServer = new Server(6969)) {
            gameServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
