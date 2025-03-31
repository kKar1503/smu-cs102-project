package parade;

import parade.controller.network.NetworkHumanPlayerController;
import parade.logger.LoggerProvider;
import parade.network.server.Server;
import parade.settings.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    static final List<NetworkHumanPlayerController> conn = new ArrayList<>();

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
