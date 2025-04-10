package parade;

import parade.common.Player;
import parade.common.state.client.ClientLobbyCloseData;
import parade.common.state.client.ClientLobbyCreateData;
import parade.common.state.client.ClientLobbyRequestListData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerLobbyClosedData;
import parade.common.state.server.ServerLobbyCreateAckData;
import parade.common.state.server.ServerLobbyListData;
import parade.controller.network.client.ClientHumanPlayerController;
import parade.controller.network.server.ServerHumanPlayerController;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.logger.impl.JsonLogger;
import parade.logger.impl.PrettyLogger;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameClient {
    private static ServerHumanPlayerController player;
    private static final Scanner sc = new Scanner(System.in);
    private static final BlockingQueue<AbstractServerData> serverDataQueue =
            new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        new Settings.Builder()
                .shouldValidateProperties(true)
                .fromClasspath("config.properties")
                .build();
        LoggerProvider.setupLogger();

        System.out.print("Enter your player name: ");
        String name = sc.nextLine();
        System.out.println("Player name: " + name);
        Player player = new Player(name);
        try (Socket socket = new Socket("localhost", 6969)) {
            try (ClientHumanPlayerController client =
                    new ClientHumanPlayerController(socket, player, serverDataQueue)) {
                AbstractServerData data;

                if (name.equals("list")) {
                    System.out.println("Listing all players...");
                    client.send(new ClientLobbyRequestListData(player));

                    data = serverDataQueue.take();
                    if (!(data instanceof ServerLobbyListData lobbyListData)) {
                        System.out.println("Invalid initial data. Closing connection.");
                        return;
                    }
                    System.out.println(lobbyListData);
                    return;
                }

                System.out.println("Sending lobby creation data...");
                client.send(new ClientLobbyCreateData(player, "Lobby", "password", 6));
                System.out.println("Sent lobby creation data to server: " + player.getName());

                data = serverDataQueue.take();
                if (!(data instanceof ServerLobbyCreateAckData lobbyCreateAckData)) {
                    System.out.println("Invalid lobby creation ack data. Closing connection.");
                    return;
                }
                if (!lobbyCreateAckData.isSuccessful()) {
                    System.out.println("Connection rejected: " + lobbyCreateAckData.getMessage());
                    return;
                } else {
                    System.out.println("Lobby created: " + lobbyCreateAckData.getLobbyId());
                }

                Thread.sleep(5_000);

                System.out.println("Closing lobby...");
                client.send(new ClientLobbyCloseData(player, lobbyCreateAckData.getLobbyId()));

                System.out.println("Waiting for lobby close acknowledgement");
                data = serverDataQueue.take();
                if (!(data instanceof ServerLobbyClosedData lobbyClosedData)) {
                    System.out.println("Invalid lobby close ack data. Closing connection.");
                    return;
                }

                System.out.println(lobbyClosedData);

                System.out.println("Disconnecting from the server...");
            } catch (IOException e) {
                System.err.println("Failed to connect to server: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sc.close();
            }
        }
    }

    //        public static void main(String[] args)
    //                throws IOException,
    //                        IllegalStateException,
    //                        UnsupportedOperationException,
    //                        ExecutionException,
    //                        InterruptedException {
    //
    //            new Settings.Builder()
    //                    .shouldValidateProperties(true)
    //                    .fromClasspath("config.properties")
    //                    .build();
    //
    //            setupLogger();
    //            Runtime.getRuntime()
    //                    .addShutdownHook(new Thread(() -> LoggerProvider.getInstance().close()));
    //            INetworkClientRenderer renderer = new BasicNetworkClientRenderer();
    //            NetworkClientRendererProvider.setInstance(renderer);
    //
    //            renderer.renderWelcome();
    //            renderer.renderPlayerNamePrompt();
    //            String name = sc.nextLine();
    //            player = new ServerHumanPlayerController(name);
    //
    //            renderer.renderMessage("Connecting to game server... (press 'x' to cancel)");
    //            ServerConnectAckData connectAckData;
    //            try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
    //                Callable<ServerConnectAckData> connectionToServerTask =
    //                        () -> {
    //                            final int maxRetries =
    //
    //     Settings.getInstance().getInt(SettingKey.CLIENT_CONNECTION_RETRIES);
    //                            int retries = 0;
    //                            while (retries <= maxRetries) {
    //                                if (retries > 0) {
    //                                    renderer.renderMessage(
    //                                            "Connection to server failed. Retrying... "
    //                                                    + "(Retries: "
    //                                                    + retries
    //                                                    + ")");
    //                                }
    //                                Thread.sleep(1000); // Simulate server taking time to start
    // game
    //                                retries++;
    //                            }
    //                            return new ServerConnectAckData(
    //                                    false, "Connection timed out. Max retries.");
    //                        };
    //
    //                Callable<ServerConnectAckData> inputTask =
    //                        () -> {
    //                            try (Terminal terminal =
    //     TerminalBuilder.builder().system(true).build()) {
    //                                terminal.enterRawMode();
    //                                while (true) {
    //                                    int key = terminal.reader().read();
    //                                    char c = (char) key;
    //
    //                                    if (c == 'x' || c == 'X') {
    //                                        return new ServerConnectAckData(
    //                                                false, "Connection closed by user.");
    //                                    }
    //                                }
    //                            }
    //                        };
    //
    //                connectAckData = executor.invokeAny(List.of(connectionToServerTask,
    // inputTask));
    //                executor.shutdownNow();
    //            }
    //
    //            if (!connectAckData.isAccepted()) {
    //                renderer.renderMessage(
    //                        "Failed to connect to server. Reason: " +
    // connectAckData.getMessage());
    //                return;
    //            } else {
    //                renderer.renderMessage("Connected to server: " + connectAckData.getMessage());
    //            }
    //
    //            boolean shouldContinue = true;
    //            while (shouldContinue) { // Main menu loop
    //                try {
    //                    renderer.renderMenu();
    //                    int userInput = sc.nextInt();
    //                    switch (userInput) {
    //                        case 1 -> createLobby();
    //                        case 2 -> joinLobby();
    //                        case 3 -> shouldContinue = false;
    //                        default -> throw new UnexpectedUserInputException();
    //                    }
    //                } catch (NoSuchElementException | UnexpectedUserInputException e) {
    //                    renderer.renderMessage("Invalid input. Please try again.");
    //                } finally {
    //                    sc.nextLine(); // Clear the buffer
    //                }
    //            }
    //
    //            renderer.renderBye();
    //        }

    //        private static void createLobby() {
    //            INetworkClientRenderer renderer = NetworkClientRendererProvider.get();
    //            renderer.renderLobbyNamePrompt();
    //            String lobbyName = sc.nextLine();
    //            renderer.renderLobbyPassword();
    //            String lobbyPassword = sc.nextLine();
    //            renderer.renderLobbyMaxPlayersPrompt();
    //            int maxPlayers;
    //            while (true) {
    //                try {
    //                    renderer.renderLobbyMaxPlayersPrompt();
    //                    int userInput = sc.nextInt();
    //                    if (userInput < 1 || userInput > 6) {
    //                        throw new UnexpectedUserInputException();
    //                    } else {
    //                        maxPlayers = userInput;
    //                        break; // Exit the loop if input is valid
    //                    }
    //                } catch (NoSuchElementException e) {
    //                    renderer.renderMessage("Invalid input. Please try again.");
    //                } catch (UnexpectedUserInputException e) {
    //                    renderer.renderMessage("Maximum players must be between 2 and 6.");
    //                } finally {
    //                    sc.nextLine(); // Clear the buffer
    //                }
    //            }
    //            renderer.renderPrompt("Confirm lobby creation (y/n): ");
    //            while (true) {
    //                String confirm = sc.nextLine();
    //                if (confirm.equalsIgnoreCase("y")) {
    //                    System.out.println("creating lobby...");
    //                    return;
    //                } else if (confirm.equalsIgnoreCase("n")) {
    //                    renderer.renderMessage("Lobby creation cancelled.");
    //                    return;
    //                } else {
    //                    renderer.renderMessage("Invalid input. Please enter 'y' or 'n'.");
    //                }
    //            }
    //        }

    //        private static void joinLobby() {}
    //
    //        private static void setupLogger() throws IOException {
    //            Settings settings = Settings.getInstance();
    //
    //            String loggerTypes = settings.get(SettingKey.LOGGER_TYPES);
    //            boolean shouldLog = settings.getBoolean(SettingKey.LOGGER_ENABLED);
    //            AbstractLogger logger;
    //            if (!shouldLog) {
    //                logger = new NopLogger();
    //            } else {
    //                String[] loggerTypesArr = loggerTypes.split(",");
    //                // Handles duplicate logger types and trim whitespace
    //                List<String> loggerTypesArrNoDuplicates =
    //                        Stream.of(loggerTypesArr).map(String::trim).distinct().toList();
    //                if (loggerTypesArrNoDuplicates.size() == 1) {
    //                    logger = determineLoggerType(loggerTypesArrNoDuplicates.getFirst());
    //                } else {
    //                    AbstractLogger[] loggers = new
    //     AbstractLogger[loggerTypesArrNoDuplicates.size()];
    //                    int i = 0;
    //                    for (String loggerType : loggerTypesArrNoDuplicates) {
    //                        loggers[i++] = determineLoggerType(loggerType);
    //                    }
    //                    logger = new MultiLogger(loggers);
    //                }
    //            }
    //            LoggerProvider.setInstance(logger);
    //            logger.log("Initialised logger");
    //        }

    private static AbstractLogger determineLoggerType(String loggerType)
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
}
