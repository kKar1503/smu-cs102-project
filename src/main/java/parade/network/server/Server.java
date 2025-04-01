package parade.network.server;

import parade.common.Lobby;
import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.exceptions.PlayerControllerInitialisationException;
import parade.common.state.client.*;
import parade.common.state.server.*;
import parade.controller.network.INetworkPlayerController;
import parade.controller.network.NetworkHumanPlayerController;
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
    private static final AbstractLogger logger = LoggerProvider.getInstance();

    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = false;

    /**
     * The holdingCell is a map of Player & NetworkHumanPlayerController objects that are waiting to
     * be assigned to a game. They are usually the ones waiting in a lobby doing nothing, pending to
     * either join a lobby, create a lobby, or do whatever funny thing they wanna do.
     */
    private final Map<Player, NetworkHumanPlayerController> holdingCell = new HashMap<>();

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
        if (dataThread.isAlive()) {
            logger.log("Server is already running");
            return;
        }

        serverSocket = new ServerSocket(port);
        logger.log("Server started on port " + port);

        running = true;
        dataThread.start();
        listenNewConnection();
    }

    private void listenNewConnection() {
        logger.log("Server now listening for incoming connections...");
        while (running) {
            try {
                logger.log("Waiting for new connection...");
                Socket socket = serverSocket.accept();
                logger.log("Client connected: " + socket.getInetAddress());

                socket.setKeepAlive(true); // Enable TCP keep-alive

                // Accept incoming connections
                NetworkHumanPlayerController controller = new NetworkHumanPlayerController(socket);
                controller.setLobbyDataQueue(clientDataQueue);
                holdingCell.put(controller.getPlayer(), controller);
                logger.log("Added new player to holding cell: " + controller.getPlayer());
            } catch (IOException e) {
                logger.log("Error accepting connection", e);
            } catch (PlayerControllerInitialisationException e) {
                logger.log("Player controller initialisation failed", e);
            } catch (NetworkFailureException e) {
                logger.log("Network failure", e);
            } catch (Exception e) {
                logger.log("Unexpected errors occurred when initialising player controller", e);
            }
        }
    }

    private void receiveClientData() {
        logger.log("Ready to receive client data");
        while (running) {
            try {
                AbstractClientData data = clientDataQueue.take();
                logger.log("Received client data: " + data);
                handleClientData(data);
            } catch (InterruptedException e) {
                logger.log("Unexpected interruption in clientDataQueue", e);
            } catch (Exception e) {
                logger.log("Error handling client data", e);
            }
        }
        logger.log("Game server data thread closed");
    }

    private void handleClientData(AbstractClientData data) {
        logger.log("Handling client data: " + data);
        try {
            switch (data) {
                case ClientLobbyRequestListData lobbyRequestListData ->
                        lobbyList(lobbyRequestListData);
                case ClientLobbyCreateData lobbyCreateData -> lobbyCreate(lobbyCreateData);
                case ClientLobbyJoinData lobbyJoinData -> lobbyJoin(lobbyJoinData);
                case ClientLobbyStartData lobbyStartData -> {}
                case ClientLobbyCloseData lobbyCloseData -> lobbyClose(lobbyCloseData);
                case ClientLobbyLeaveData lobbyLeaveData -> lobbyLeave(lobbyLeaveData);
                default -> logger.log("Received unsupported data type: " + data);
            }
        } catch (NetworkFailureException e) {
            logger.log("Network failure while sending server data to client", e);
        }
    }

    private void lobbyList(ClientLobbyRequestListData lobbyRequestListData)
            throws NetworkFailureException {
        NetworkHumanPlayerController callerController = getPrisoner(lobbyRequestListData);
        if (callerController == null) {
            logger.log(
                    "Unknown caller: " + lobbyRequestListData.getCaller() + " - ignoring request");
            return;
        }

        Lobby[] lobbies =
                lobbyMap.values().stream().map(NetworkGameEngine::getLobby).toArray(Lobby[]::new);
        callerController.send(new ServerLobbyListData(lobbies));
    }

    private void lobbyCreate(ClientLobbyCreateData lobbyCreateData) throws NetworkFailureException {
        NetworkHumanPlayerController callerController = getPrisoner(lobbyCreateData);
        if (callerController == null) {
            logger.log("Unknown caller: " + lobbyCreateData.getCaller() + " - ignoring request");
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

    private void lobbyClose(ClientLobbyCloseData lobbyCloseData) throws NetworkFailureException {
        NetworkGameEngine gameEngine = lobbyMap.get(lobbyCloseData.getLobbyId());
        if (gameEngine == null) {
            logger.log("Lobby not found: " + lobbyCloseData.getLobbyId());
            return;
        }

        Lobby lobby = gameEngine.getLobby();
        Player owner = lobby.getOwner();
        if (owner.equals(lobbyCloseData.getCaller())) {
            logger.log("Closing lobby: " + lobbyCloseData.getLobbyId());

            lobbyMap.remove(lobbyCloseData.getLobbyId());
            logger.log("Lobby removed from game server: " + lobbyCloseData.getLobbyId());

            ServerLobbyClosedData lobbyClosedData =
                    new ServerLobbyClosedData(
                            lobbyCloseData.getLobbyId(),
                            "Lobby closed by owner: " + owner.getName());

            for (INetworkPlayerController controller : gameEngine.getControllers()) {
                if (controller instanceof NetworkHumanPlayerController humanController) {
                    humanController.send(lobbyClosedData);
                    logger.logf("Informed player %s on lobby closure", humanController);
                    holdingCell.put(humanController.getPlayer(), humanController);
                    logger.logf("Moved player %s back to holding cell", humanController);
                }
            }
        } else {
            logger.log("Player attempting to close lobby is not the owner: " + lobbyCloseData);
        }
    }

    private void lobbyLeave(ClientLobbyLeaveData lobbyLeaveData) throws NetworkFailureException {
        NetworkGameEngine gameEngine = lobbyMap.get(lobbyLeaveData.getLobbyId());
        if (gameEngine == null) {
            logger.log("Lobby not found: " + lobbyLeaveData.getLobbyId());
            return;
        }

        NetworkHumanPlayerController controller =
                (NetworkHumanPlayerController)
                        gameEngine.removePlayerController(lobbyLeaveData.getCaller());
        if (controller == null) {
            logger.log("Player not found in lobby: " + lobbyLeaveData.getCaller());
            return;
        } else {
            logger.log("Removed player from lobby: " + controller);
        }

        ServerLobbyPlayerLeftData lobbyPlayerLeftData =
                new ServerLobbyPlayerLeftData(lobbyLeaveData.getCaller());
        for (INetworkPlayerController player : gameEngine.getControllers()) {
            if (player instanceof NetworkHumanPlayerController humanController) {
                humanController.send(lobbyPlayerLeftData);
                logger.logf("Informed player %s on lobby player left", humanController);
            }
        }

        controller.send(lobbyPlayerLeftData);
        logger.logf("Informed player %s that they have successfully left the lobby", controller);

        holdingCell.put(controller.getPlayer(), controller);
        logger.log("Placed player back in holding cell: " + controller);
    }

    private void lobbyJoin(ClientLobbyJoinData lobbyJoinData) throws NetworkFailureException {
        NetworkHumanPlayerController callerController = getPrisoner(lobbyJoinData);
        if (callerController == null) {
            logger.log("Unknown caller: " + lobbyJoinData.getCaller() + " - ignoring request");
            return;
        }

        NetworkGameEngine gameEngine = lobbyMap.get(lobbyJoinData.getLobbyId());
        if (gameEngine == null) {
            logger.log("Lobby not found: " + lobbyJoinData.getLobbyId());
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

        for (INetworkPlayerController player : gameEngine.getControllers()) {
            if (player instanceof NetworkHumanPlayerController humanController) {
                humanController.send(new ServerLobbyPlayerJoinedData(callerController.getPlayer()));
                logger.logf(
                        "Informed player %s on lobby player %s joined",
                        humanController, callerController);
            }
        }

        gameEngine.addPlayerController(holdingCell.remove(callerController.getPlayer()));
        logger.log("Moved player to lobby: " + callerController);
        ServerLobbyJoinAckData lobbyJoinAckData =
                new ServerLobbyJoinAckData(lobby.getId(), true, "Joined lobby: " + lobby.getId());
        callerController.send(lobbyJoinAckData);
    }

    private NetworkHumanPlayerController getPrisoner(AbstractClientData data) {
        NetworkHumanPlayerController controller = holdingCell.get(data.getCaller());
        if (controller == null) {
            logger.log("Player not found in holding cell: " + data.getCaller());
            return null;
        }
        return controller;
    }

    @Override
    public void close() throws Exception {
        if (!running) {
            logger.log("Server is already closed");
            return;
        }

        running = false;
        serverSocket.close();
        logger.log("Server closed");

        for (NetworkHumanPlayerController controller : holdingCell.values()) {
            try {
                controller.close();
            } catch (Exception e) {
                logger.log(
                        "Error closing player controller: " + controller.getPlayer().getName(), e);
            }
        }

        holdingCell.clear();
        logger.log("All player controllers closed");

        clientDataQueue.clear();
        logger.log("Client data queue cleared");

        dataThread.join(5_000);

        if (dataThread.isAlive()) {
            logger.log("Data thread did not terminate in time");
            dataThread.interrupt();
        } else {
            logger.log("Data thread terminated");
        }

        logger.log("Server closed successfully");
    }
}
