package parade.network.server;

import parade.common.Lobby;
import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.exceptions.PlayerControllerInitialisationException;
import parade.common.state.client.*;
import parade.common.state.server.*;
import parade.controller.network.server.IServerPlayerController;
import parade.controller.network.server.ServerHumanPlayerController;
import parade.engine.NetworkGameEngine;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements AutoCloseable {
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();

    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    /**
     * The holdingCell is a map of Player & ServerHumanPlayerController objects that are waiting to
     * be assigned to a game. They are usually the ones waiting in a lobby doing nothing, pending to
     * either join a lobby, create a lobby, or do whatever funny thing they wanna do.
     */
    private final Map<Player, ServerHumanPlayerController> holdingCell = new HashMap<>();

    /**
     * The lobbyMap is a map of lobby IDs and NetworkGameEngine objects that are currently active.
     *
     * <p>Once a game has been started / closed, the NetworkGameEngine object will be removed from
     * this map.
     */
    private final Map<String, NetworkGameEngine> lobbyMap = new HashMap<>();

    /**
     * The clientDataQueue is a queue of AbstractClientData objects received from all the clients
     * connected and placed in the holdingCell.
     */
    private final BlockingQueue<AbstractClientData> clientDataQueue = new LinkedBlockingQueue<>();

    private final Thread dataThread = Thread.ofVirtual().unstarted(this::receiveClientData);

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        if (dataThread.isAlive() || running || (serverSocket != null && !serverSocket.isClosed())) {
            LOGGER.log("Server is already running");
            return;
        }

        serverSocket = new ServerSocket(port);
        LOGGER.log("Server started on port " + port);

        running = true;
        dataThread.start();
        LOGGER.log("Thread started to listening to receive client data");

        listenNewConnection();
    }

    private void listenNewConnection() {
        LOGGER.log("Server now listening for incoming connections...");
        while (running) {
            try {
                LOGGER.log("Waiting for new connection...");
                Socket socket = serverSocket.accept();
                LOGGER.log("Client connected: " + socket.getInetAddress());

                socket.setKeepAlive(true); // Enable TCP keep-alive

                // Accept incoming connections
                ServerHumanPlayerController controller =
                        new ServerHumanPlayerController(socket, clientDataQueue);
                holdingCell.put(controller.getPlayer(), controller);
                LOGGER.log("Added new player to holding cell: " + controller.getPlayer());
            } catch (IOException e) {
                LOGGER.log("Error accepting connection", e);
            } catch (PlayerControllerInitialisationException e) {
                LOGGER.log("Player controller initialisation failed", e);
            } catch (NetworkFailureException e) {
                LOGGER.log("Network failure", e);
            } catch (Exception e) {
                LOGGER.log("Unexpected errors occurred when initialising player controller", e);
            }
        }
    }

    private void receiveClientData() {
        LOGGER.log("Ready to receive client data");
        while (running) {
            try {
                AbstractClientData data = clientDataQueue.take();
                LOGGER.log("Received client data: " + data);
                handleClientData(data);
            } catch (InterruptedException e) {
                LOGGER.log("Unexpected interruption in clientDataQueue", e);
            } catch (Exception e) {
                LOGGER.log("Error handling client data", e);
            }
        }
        LOGGER.log("Game server data thread closed");
    }

    private void handleClientData(AbstractClientData data) {
        LOGGER.log("Handling client data: " + data);
        try {
            switch (data) {
                case ClientLobbyRequestListData lobbyRequestListData ->
                        lobbyList(lobbyRequestListData);
                case ClientLobbyCreateData lobbyCreateData -> lobbyCreate(lobbyCreateData);
                case ClientLobbyJoinData lobbyJoinData -> lobbyJoin(lobbyJoinData);
                case ClientLobbyStartData lobbyStartData -> {}
                case ClientLobbyCloseData lobbyCloseData -> lobbyClose(lobbyCloseData);
                case ClientLobbyLeaveData lobbyLeaveData -> lobbyLeave(lobbyLeaveData);
                default -> LOGGER.log("Received unsupported data type: " + data);
            }
        } catch (NetworkFailureException | IOException e) {
            LOGGER.log("Network failure while sending server data to client", e);
        }
    }

    private void lobbyList(ClientLobbyRequestListData lobbyRequestListData)
            throws NetworkFailureException, IOException {
        ServerHumanPlayerController callerController = getPrisoner(lobbyRequestListData);
        if (callerController == null) {
            LOGGER.log(
                    "Unknown caller: " + lobbyRequestListData.getCaller() + " - ignoring request");
            return;
        }

        Lobby[] lobbies =
                lobbyMap.values().stream().map(NetworkGameEngine::getLobby).toArray(Lobby[]::new);
        callerController.send(new ServerLobbyListData(lobbies));
    }

    private void lobbyCreate(ClientLobbyCreateData lobbyCreateData)
            throws NetworkFailureException, IOException {
        ServerHumanPlayerController callerController = getPrisoner(lobbyCreateData);
        if (callerController == null) {
            LOGGER.log("Unknown caller: " + lobbyCreateData.getCaller() + " - ignoring request");
            return;
        }

        Lobby lobby =
                new Lobby(
                        lobbyCreateData.getLobbyName(),
                        lobbyCreateData.getLobbyPassword(),
                        2,
                        lobbyCreateData.getMaxPlayers(),
                        callerController.getPlayer());
        NetworkGameEngine gameEngine = new NetworkGameEngine(lobby, callerController);
        holdingCell.remove(callerController.getPlayer());
        lobbyMap.put(lobby.getId(), gameEngine);
        callerController.send(
                new ServerLobbyCreateAckData(
                        lobby.getId(), true, "Lobby created: " + lobby.getId()));
    }

    private void lobbyClose(ClientLobbyCloseData lobbyCloseData)
            throws NetworkFailureException, IOException {
        NetworkGameEngine gameEngine = lobbyMap.get(lobbyCloseData.getLobbyId());
        if (gameEngine == null) {
            LOGGER.log("Lobby not found: " + lobbyCloseData.getLobbyId());
            return;
        }

        Lobby lobby = gameEngine.getLobby();
        Player owner = lobby.getOwner();
        if (owner.equals(lobbyCloseData.getCaller())) {
            LOGGER.log("Closing lobby: " + lobbyCloseData.getLobbyId());

            lobbyMap.remove(lobbyCloseData.getLobbyId());
            LOGGER.log("Lobby removed from game server: " + lobbyCloseData.getLobbyId());

            ServerLobbyClosedData lobbyClosedData =
                    new ServerLobbyClosedData(
                            lobbyCloseData.getLobbyId(),
                            "Lobby closed by owner: " + owner.getName());

            for (IServerPlayerController controller : gameEngine.getControllers()) {
                if (controller instanceof ServerHumanPlayerController humanController) {
                    humanController.send(lobbyClosedData);
                    LOGGER.logf("Informed player %s on lobby closure", humanController);
                    holdingCell.put(humanController.getPlayer(), humanController);
                    LOGGER.logf("Moved player %s back to holding cell", humanController);
                }
            }
        } else {
            LOGGER.log("Player attempting to close lobby is not the owner: " + lobbyCloseData);
        }
    }

    private void lobbyLeave(ClientLobbyLeaveData lobbyLeaveData)
            throws NetworkFailureException, IOException {
        NetworkGameEngine gameEngine = lobbyMap.get(lobbyLeaveData.getLobbyId());
        if (gameEngine == null) {
            LOGGER.log("Lobby not found: " + lobbyLeaveData.getLobbyId());
            return;
        }

        ServerHumanPlayerController controller =
                (ServerHumanPlayerController)
                        gameEngine.removePlayerController(lobbyLeaveData.getCaller());
        if (controller == null) {
            LOGGER.log("Player not found in lobby: " + lobbyLeaveData.getCaller());
            return;
        } else {
            LOGGER.log("Removed player from lobby: " + controller);
        }

        ServerLobbyPlayerLeftData lobbyPlayerLeftData =
                new ServerLobbyPlayerLeftData(lobbyLeaveData.getCaller());
        for (IServerPlayerController player : gameEngine.getControllers()) {
            if (player instanceof ServerHumanPlayerController humanController) {
                humanController.send(lobbyPlayerLeftData);
                LOGGER.logf("Informed player %s on lobby player left", humanController);
            }
        }

        controller.send(lobbyPlayerLeftData);
        LOGGER.logf("Informed player %s that they have successfully left the lobby", controller);

        holdingCell.put(controller.getPlayer(), controller);
        LOGGER.log("Placed player back in holding cell: " + controller);
    }

    private void lobbyJoin(ClientLobbyJoinData lobbyJoinData)
            throws NetworkFailureException, IOException {
        ServerHumanPlayerController callerController = getPrisoner(lobbyJoinData);
        if (callerController == null) {
            LOGGER.log("Unknown caller: " + lobbyJoinData.getCaller() + " - ignoring request");
            return;
        }

        NetworkGameEngine gameEngine = lobbyMap.get(lobbyJoinData.getLobbyId());
        if (gameEngine == null) {
            LOGGER.log("Lobby not found: " + lobbyJoinData.getLobbyId());
            return;
        }

        Lobby lobby = gameEngine.getLobby();
        if (lobby.isFull()) {
            callerController.send(
                    new ServerLobbyJoinAckData(lobby.getId(), false, "Lobby is full"));
            return;
        }

        if (!lobby.isPublic() && !lobby.getPassword().equals(lobbyJoinData.getLobbyPassword())) {
            callerController.send(
                    new ServerLobbyJoinAckData(
                            lobby.getId(), false, "Incorrect password for lobby"));
            return;
        }

        for (IServerPlayerController player : gameEngine.getControllers()) {
            if (player instanceof ServerHumanPlayerController humanController) {
                humanController.send(new ServerLobbyPlayerJoinedData(callerController.getPlayer()));
                LOGGER.logf(
                        "Informed player %s on lobby player %s joined",
                        humanController, callerController);
            }
        }

        gameEngine.addPlayerController(holdingCell.remove(callerController.getPlayer()));
        LOGGER.log("Moved player to lobby: " + callerController);
        ServerLobbyJoinAckData lobbyJoinAckData =
                new ServerLobbyJoinAckData(lobby.getId(), true, "Joined lobby: " + lobby.getId());
        callerController.send(lobbyJoinAckData);
    }

    private ServerHumanPlayerController getPrisoner(AbstractClientData data) {
        ServerHumanPlayerController controller = holdingCell.get(data.getCaller());
        if (controller == null) {
            LOGGER.log("Player not found in holding cell: " + data.getCaller());
            return null;
        }
        return controller;
    }

    @Override
    public void close() throws Exception {
        if (!running) {
            LOGGER.log("Server is already closed");
            return;
        }

        running = false;
        serverSocket.close();
        LOGGER.log("Server closed");

        for (ServerHumanPlayerController controller : holdingCell.values()) {
            try {
                controller.close();
            } catch (Exception e) {
                LOGGER.log(
                        "Error closing player controller: " + controller.getPlayer().getName(), e);
            }
        }

        holdingCell.clear();
        LOGGER.log("All player controllers closed");

        clientDataQueue.clear();
        LOGGER.log("Client data queue cleared");

        dataThread.join(5_000);

        if (dataThread.isAlive()) {
            LOGGER.log("Data thread did not terminate in time");
            dataThread.interrupt();
        } else {
            LOGGER.log("Data thread terminated");
        }

        LOGGER.log("Server closed successfully");
    }
}
