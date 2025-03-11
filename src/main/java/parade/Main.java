package parade;

import parade.common.Server;
import parade.server.BasicGameServer;
import parade.settings.SettingKey;
import parade.settings.Settings;
import parade.textrenderer.DebugRenderer;
import parade.textrenderer.DebugRendererProvider;
import parade.textrenderer.TextRendererProvider;
import parade.textrenderer.impl.*;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static Server server;

    public static void main(String[] args)
            throws IOException, IllegalStateException, UnsupportedOperationException {

        Settings settings =
                new Settings.Builder()
                        .shouldValidateProperties(true)
                        .fromClasspath("config.properties")
                        .build();

        // Currently only supporting 1 debug renderer
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

        String gameplayMode = settings.get(SettingKey.GAMEPLAY_MODE);
        debugRenderer.debug("Gameplay is starting in " + gameplayMode + " mode");
        switch (gameplayMode) {
            case "local":
                TextRendererProvider.setInstance(new BasicTextRenderer());
                debugRenderer.debug("Initialised basic text renderer");
                server = new BasicGameServer();
                debugRenderer.debug("Initialised basic game server");
                break;
            case "network":
                throw new UnsupportedOperationException("Network mode is not yet supported");
            default:
                throw new IllegalStateException(
                        "Unknown gameplay mode in settings: " + gameplayMode);
        }

        TextRendererProvider.getInstance().renderWelcome();
        Scanner scanner = new Scanner(System.in);
        debugRenderer.debug("Prompting user to start game in menu");
        while (true) {
            TextRendererProvider.getInstance().renderMenu();
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input != 1 && input != 2) {
                    System.out.println("Invalid input, please only type only 1 or 2.");
                    continue;
                }

                if (input == 1) {
                    debugRenderer.debug("User is starting the game");
                    startGame();
                } else {
                    debugRenderer.debug("User is exiting the game");
                    TextRendererProvider.getInstance().renderBye();
                }
                break;
            } catch (NoSuchElementException e) {
                debugRenderer.debug("Invalid input received", e);
                System.out.println("Invalid input, please try again.");
            }
        }
    }

    static void startGame() {
        server.waitForPlayersLobby();
        server.startGame();
    }
}
