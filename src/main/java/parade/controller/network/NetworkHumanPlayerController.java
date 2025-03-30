package parade.controller.network;

import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.exceptions.PlayerControllerInitialisationException;
import parade.common.state.client.*;
import parade.common.state.server.*;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkHumanPlayerController implements INetworkPlayerController {
    private static final AbstractLogger logger = LoggerProvider.getInstance();

    private final Player player;
    private volatile boolean running = true;

    // Socket and streams for communication with the client
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    // Queue to hold incoming data from the client
    private BlockingQueue<AbstractClientData> lobbyDataQueue = new LinkedBlockingQueue<>();
    private boolean hasReplacedInitialQueue = false;

    // Thread to listen for incoming data from the client
    private final Thread listenThread;

    public NetworkHumanPlayerController(Socket socket)
            throws IOException,
                    NetworkFailureException,
                    PlayerControllerInitialisationException,
                    InterruptedException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush(); // Flush the stream to ensure the TCP header is sent first
        this.in = new ObjectInputStream(socket.getInputStream());

        this.player = initialHandShake();

        this.listenThread = Thread.ofVirtual().start(this::listenForClientData);

        logger.log("NetworkHumanPlayerController created for player: " + this.player.getName());
    }

    private Player initialHandShake()
            throws IOException,
                    NetworkFailureException,
                    PlayerControllerInitialisationException,
                    InterruptedException {
        AbstractClientData data;
        try {
            data = readSingleInput(10_000);
        } catch (NetworkFailureException | IOException e) {
            // Failed to receive handshake data
            logger.log("Failed to receive connect data", e);
            close();
            throw new PlayerControllerInitialisationException("Failed to receive connect data", e);
        }
        if (data == null) {
            logger.log("Timed out waiting for connect data");
            send(new ServerConnectAckData(false, "Timed out waited 10,000ms"));
            close();
            throw new PlayerControllerInitialisationException("Timed out receiving connect data");
        }
        if (!(data instanceof ClientConnectData connectData)) {
            logger.log("Invalid connect data, data type mismatch. Closing connection.");
            send(new ServerConnectAckData(false, "Invalid connect data"));
            close();
            throw new PlayerControllerInitialisationException(
                    "Incoming data is not of type ClientConnectData");
        }

        Player caller = connectData.getCaller();
        if (caller == null || caller.getName() == null || caller.getName().isEmpty()) {
            logger.log("Invalid connect data, player name is invalid. Closing connection.");
            send(new ServerConnectAckData(false, "Invalid connect data"));
            close();
            throw new PlayerControllerInitialisationException("Invalid player name");
        }

        logger.logf("Initial handshake successful, player name: %s", caller.getName());
        send(new ServerConnectAckData(true, "Connected to the server! " + caller.getName()));
        return caller;
    }

    @Override
    public void send(AbstractServerData serverData) throws NetworkFailureException {
        try {
            out.writeObject(serverData);
            out.flush();
        } catch (IOException e) {
            throw new NetworkFailureException("Failed to write server data to Socket", e);
        }
    }

    @Override
    public void setLobbyDataQueue(BlockingQueue<AbstractClientData> lobbyDataQueue) {
        if (!hasReplacedInitialQueue) {
            logger.log("Replacing initial client data queue with new one");
            hasReplacedInitialQueue = true;
            // Move all the items from the old queue to the new one
            while (!this.lobbyDataQueue.isEmpty()) {
                lobbyDataQueue.offer(this.lobbyDataQueue.poll());
            }
        }
        this.lobbyDataQueue = lobbyDataQueue;
    }

    private AbstractClientData readSingleInput(int timeInMs)
            throws IOException, NetworkFailureException {
        AbstractClientData clientData = null;
        try {
            socket.setSoTimeout(timeInMs);
            clientData = readSingleInput();
        } catch (SocketTimeoutException e) {
            logger.log("Socket timeout while waiting for client data, returning null");
        } finally {
            socket.setSoTimeout(0);
        }
        return clientData;
    }

    private AbstractClientData readSingleInput() throws IOException, NetworkFailureException {
        AbstractClientData clientData;
        try {
            Object inputObject = in.readObject();
            if (inputObject instanceof AbstractClientData receivedClientData) {
                clientData = receivedClientData;
                logger.log("Received client data type: " + clientData);
            } else {
                throw new NetworkFailureException(
                        "Server received data is not of type AbstractClientData");
            }
        } catch (ClassNotFoundException e) {
            throw new NetworkFailureException("Invalid class for object input", e);
        }
        return clientData;
    }

    public void listenForClientData() {
        logger.logf("NetworkHumanPlayerController (%s) listening for data", player.getName());
        try {
            while (running) {
                logger.logf("NetworkHumanPlayerController (%s) waiting for data", player.getName());
                try {
                    AbstractClientData clientData = readSingleInput();
                    if (clientData == null) {
                        logger.log("Received null client data, ignoring...");
                    } else {
                        // controller should not internally decide to act upon the data, it
                        // should instead send the data over to the lobbyDataQueue and let the game
                        // engine decides how it should handle the player state, because there is a
                        // possibility that there are double sends of data etc, and the game engine
                        // should be the one to decide how to handle that. Other situation like when
                        // it's not the player's turn to act, but like something went wrong and some
                        // of the player action data got sent over...
                        logger.log("Forwarding client data to lobby data queue...");
                        lobbyDataQueue.offer(clientData);
                    }
                } catch (NetworkFailureException e) {
                    logger.log("Failed to read data from input stream", e);
                }
            }
        } catch (EOFException e) {
            logger.log("Connection closed by client");
        } catch (IOException e) {
            logger.log("I/O error occurred", e);
        } catch (Exception e) {
            logger.log("Unexpected error occurred", e);
        } finally {
            try {
                close();
            } catch (IOException e) {
                logger.log("Error closing connection", e);
            } catch (InterruptedException e) {
                logger.log("Thread interrupted while closing connection", e);
            }
        }
        logger.logf("NetworkHumanPlayerController (%s) listenThread closed", player.getName());
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void close() throws IOException, InterruptedException {
        if (!running) {
            return;
        }

        running = false;
        socket.close();
        in.close();

        logger.logf("NetworkHumanPlayerController (%s) closed", player.getName());
    }
}
