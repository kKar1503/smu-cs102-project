package parade;

import parade.common.Player;
import parade.common.state.client.ClientConnectData;
import parade.common.state.client.ClientLobbyCreateData;
import parade.common.state.client.ClientLobbyRequestListData;
import parade.common.state.server.ServerConnectAckData;
import parade.common.state.server.ServerLobbyCreateAckData;
import parade.common.state.server.ServerLobbyListData;
import parade.controller.network.NetworkHumanPlayerController;
import parade.logger.AbstractLogger;
import parade.logger.impl.JsonLogger;
import parade.logger.impl.PrettyLogger;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class GameClient {
    private static NetworkHumanPlayerController player;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 6969)) {
            System.out.println("Connected to server: " + socket.getInetAddress());
            System.out.print("Enter your player ID: ");
            String id = sc.nextLine();
            System.out.println("Player ID: " + id);

            Player player = new Player(id);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(new ClientConnectData(player));
            out.flush();
            System.out.println("Sent player data to server: " + player.getName());

            System.out.println("waiting for ack");
            Object data = in.readObject();
            if (!(data instanceof ServerConnectAckData connectAckData)) {
                System.out.println("Invalid initial data. Closing connection.");
                socket.close();
                return;
            }
            if (!connectAckData.isAccepted()) {
                System.out.println("Connection rejected: " + connectAckData.getMessage());
                socket.close();
                return;
            } else {
                System.out.println("Connected to server: " + connectAckData.getMessage());
            }

            Thread.sleep(2_000);

            if (id.equals("list")) {
                System.out.println("Listing all players...");
                out.writeObject(new ClientLobbyRequestListData(player));
                out.flush();

                data = in.readObject();
                if (!(data instanceof ServerLobbyListData lobbyListData)) {
                    System.out.println("Invalid initial data. Closing connection.");
                    socket.close();
                    return;
                }
                System.out.println(lobbyListData);
                return;
            }

            System.out.println("Sending lobby creation data...");
            out.writeObject(new ClientLobbyCreateData(player, "Lobby", "password", 6));
            out.flush();
            System.out.println("Sent lobby creation data to server: " + player.getName());

            data = in.readObject();
            if (!(data instanceof ServerLobbyCreateAckData lobbyCreateAckData)) {
                System.out.println("Invalid lobby creation ack data. Closing connection.");
                socket.close();
                return;
            }
            if (!lobbyCreateAckData.isSuccessful()) {
                System.out.println("Connection rejected: " + lobbyCreateAckData.getMessage());
                socket.close();
                return;
            } else {
                System.out.println("Lobby created: " + lobbyCreateAckData.getLobbyId());
            }

            Thread.sleep(2_000);

            System.out.println("Disconnecting from the server...");
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    //    public static void main(String[] args)
    //            throws IOException,
    //                    IllegalStateException,
    //                    UnsupportedOperationException,
    //                    ExecutionException,
    //                    InterruptedException {
    //
    //        new Settings.Builder()
    //                .shouldValidateProperties(true)
    //                .fromClasspath("config.properties")
    //                .build();
    //
    //        setupLogger();
    //        Runtime.getRuntime()
    //                .addShutdownHook(new Thread(() -> LoggerProvider.getInstance().close()));
    //        INetworkClientRenderer renderer = new BasicNetworkClientRenderer();
    //        NetworkClientRendererProvider.setInstance(renderer);
    //
    //        renderer.renderWelcome();
    //        renderer.renderPlayerNamePrompt();
    //        String name = sc.nextLine();
    //        player = new NetworkHumanPlayerController(name);
    //
    //        renderer.renderMessage("Connecting to game server... (press 'x' to cancel)");
    //        ServerConnectAckData connectAckData;
    //        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
    //            Callable<ServerConnectAckData> connectionToServerTask =
    //                    () -> {
    //                        final int maxRetries =
    //
    // Settings.getInstance().getInt(SettingKey.CLIENT_CONNECTION_RETRIES);
    //                        int retries = 0;
    //                        while (retries <= maxRetries) {
    //                            if (retries > 0) {
    //                                renderer.renderMessage(
    //                                        "Connection to server failed. Retrying... "
    //                                                + "(Retries: "
    //                                                + retries
    //                                                + ")");
    //                            }
    //                            Thread.sleep(1000); // Simulate server taking time to start game
    //                            retries++;
    //                        }
    //                        return new ServerConnectAckData(
    //                                false, "Connection timed out. Max retries.");
    //                    };
    //
    //            Callable<ServerConnectAckData> inputTask =
    //                    () -> {
    //                        try (Terminal terminal =
    // TerminalBuilder.builder().system(true).build()) {
    //                            terminal.enterRawMode();
    //                            while (true) {
    //                                int key = terminal.reader().read();
    //                                char c = (char) key;
    //
    //                                if (c == 'x' || c == 'X') {
    //                                    return new ServerConnectAckData(
    //                                            false, "Connection closed by user.");
    //                                }
    //                            }
    //                        }
    //                    };
    //
    //            connectAckData = executor.invokeAny(List.of(connectionToServerTask, inputTask));
    //            executor.shutdownNow();
    //        }
    //
    //        if (!connectAckData.isAccepted()) {
    //            renderer.renderMessage(
    //                    "Failed to connect to server. Reason: " + connectAckData.getMessage());
    //            return;
    //        } else {
    //            renderer.renderMessage("Connected to server: " + connectAckData.getMessage());
    //        }
    //
    //        boolean shouldContinue = true;
    //        while (shouldContinue) { // Main menu loop
    //            try {
    //                renderer.renderMenu();
    //                int userInput = sc.nextInt();
    //                switch (userInput) {
    //                    case 1 -> createLobby();
    //                    case 2 -> joinLobby();
    //                    case 3 -> shouldContinue = false;
    //                    default -> throw new UnexpectedUserInputException();
    //                }
    //            } catch (NoSuchElementException | UnexpectedUserInputException e) {
    //                renderer.renderMessage("Invalid input. Please try again.");
    //            } finally {
    //                sc.nextLine(); // Clear the buffer
    //            }
    //        }
    //
    //        renderer.renderBye();
    //    }
    //
    //    private static void createLobby() {
    //        INetworkClientRenderer renderer = NetworkClientRendererProvider.get();
    //        renderer.renderLobbyNamePrompt();
    //        String lobbyName = sc.nextLine();
    //        renderer.renderLobbyPassword();
    //        String lobbyPassword = sc.nextLine();
    //        renderer.renderLobbyMaxPlayersPrompt();
    //        int maxPlayers;
    //        while (true) {
    //            try {
    //                renderer.renderLobbyMaxPlayersPrompt();
    //                int userInput = sc.nextInt();
    //                if (userInput < 1 || userInput > 6) {
    //                    throw new UnexpectedUserInputException();
    //                } else {
    //                    maxPlayers = userInput;
    //                    break; // Exit the loop if input is valid
    //                }
    //            } catch (NoSuchElementException e) {
    //                renderer.renderMessage("Invalid input. Please try again.");
    //            } catch (UnexpectedUserInputException e) {
    //                renderer.renderMessage("Maximum players must be between 2 and 6.");
    //            } finally {
    //                sc.nextLine(); // Clear the buffer
    //            }
    //        }
    //        renderer.renderPrompt("Confirm lobby creation (y/n): ");
    //        while (true) {
    //            String confirm = sc.nextLine();
    //            if (confirm.equalsIgnoreCase("y")) {
    //                System.out.println("creating lobby...");
    //                return;
    //            } else if (confirm.equalsIgnoreCase("n")) {
    //                renderer.renderMessage("Lobby creation cancelled.");
    //                return;
    //            } else {
    //                renderer.renderMessage("Invalid input. Please enter 'y' or 'n'.");
    //            }
    //        }
    //    }
    //
    //    private static void joinLobby() {}
    //
    //    private static void setupLogger() throws IOException {
    //        Settings settings = Settings.getInstance();
    //
    //        String loggerTypes = settings.get(SettingKey.LOGGER_TYPES);
    //        boolean shouldLog = settings.getBoolean(SettingKey.LOGGER_ENABLED);
    //        AbstractLogger logger;
    //        if (!shouldLog) {
    //            logger = new NopLogger();
    //        } else {
    //            String[] loggerTypesArr = loggerTypes.split(",");
    //            // Handles duplicate logger types and trim whitespace
    //            List<String> loggerTypesArrNoDuplicates =
    //                    Stream.of(loggerTypesArr).map(String::trim).distinct().toList();
    //            if (loggerTypesArrNoDuplicates.size() == 1) {
    //                logger = determineLoggerType(loggerTypesArrNoDuplicates.getFirst());
    //            } else {
    //                AbstractLogger[] loggers = new
    // AbstractLogger[loggerTypesArrNoDuplicates.size()];
    //                int i = 0;
    //                for (String loggerType : loggerTypesArrNoDuplicates) {
    //                    loggers[i++] = determineLoggerType(loggerType);
    //                }
    //                logger = new MultiLogger(loggers);
    //            }
    //        }
    //        LoggerProvider.setInstance(logger);
    //        logger.log("Initialised logger");
    //    }

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
