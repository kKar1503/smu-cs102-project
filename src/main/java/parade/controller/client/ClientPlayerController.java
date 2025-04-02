package parade.controller.client;

import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.state.client.AbstractClientData;
import parade.common.state.client.ClientConnectData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerConnectAckData;
import parade.controller.IPlayerController;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

public class ClientPlayerController implements IPlayerController, AutoCloseable {
    private static final AbstractLogger logger = LoggerProvider.getInstance();

    private final Player player;

    private final int port;
    private final String host;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile boolean running = false;

    private final Thread dataThread = Thread.ofVirtual().unstarted(this::listenForServerData);

    private BlockingQueue<AbstractServerData> serverDataQueue = null;

    public ClientPlayerController(String host, int port, Player player) {
        this.port = port;
        this.host = host;
        this.player = player;
    }

    public void start(BlockingQueue<AbstractServerData> serverDataQueue)
            throws IOException, NetworkFailureException, InterruptedException {
        if (dataThread.isAlive()) {
            logger.log("Client is already running");
            return;
        }

        running = true;
        this.serverDataQueue = serverDataQueue;
        socket = new Socket(host, port);
        logger.log("Connected to " + host + ":" + port + " as " + player.getName());
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush(); // Flush the stream to ensure the header is sent
        in = new ObjectInputStream(socket.getInputStream());
        initiateHandshake();
        dataThread.start();
    }

    private void initiateHandshake()
            throws IOException, NetworkFailureException, InterruptedException {
        logger.log("Initiating handshake with server...");
        send(new ClientConnectData(player));
        AbstractServerData data;
        try {
            data = readSingleInput(10_000);
        } catch (NetworkFailureException | IOException e) {
            logger.log("Failed to receive connection ack data", e);
            close();
            throw new NetworkFailureException("Failed to receive connection ack data", e);
        }
        if (data == null) {
            logger.log("Timed out waiting for connection ack data");
            close();
            throw new NetworkFailureException("Timed out waiting for connection ack data");
        }
        if (!(data instanceof ServerConnectAckData connectAckData)) {
            logger.log("Received unexpected data type during handshake: " + data);
            close();
            throw new NetworkFailureException("Received unexpected data type during handshake");
        }
        if (!connectAckData.isAccepted()) {
            logger.log("Handshake failed: " + connectAckData.getMessage());
            close();
            throw new NetworkFailureException("Handshake failed: " + connectAckData.getMessage());
        }

        logger.log("Handshake successful: " + connectAckData.getMessage());
    }

    private AbstractServerData readSingleInput(int timeInMs)
            throws IOException, NetworkFailureException {
        AbstractServerData serverData = null;
        try {
            socket.setSoTimeout(timeInMs);
            serverData = readSingleInput();
        } catch (SocketTimeoutException e) {
            logger.log("Socket timeout while waiting for server data, returning null");
        } finally {
            socket.setSoTimeout(0);
        }
        return serverData;
    }

    private AbstractServerData readSingleInput() throws IOException, NetworkFailureException {
        AbstractServerData serverData;
        try {
            Object inputObject = in.readObject();
            if (inputObject instanceof AbstractServerData receivedServerData) {
                serverData = receivedServerData;
                logger.log("Received server data type: " + serverData);
            } else {
                throw new NetworkFailureException(
                        "Client received data is not of type AbstractServerData");
            }
        } catch (ClassNotFoundException e) {
            throw new NetworkFailureException("Invalid class for object input", e);
        }
        return serverData;
    }

    private void listenForServerData() {
        logger.log("Client listening for data");
        try {
            while (running) {
                logger.log("Client waiting for data");
                try {
                    AbstractServerData serverData = readSingleInput();
                    if (serverData == null) {
                        logger.log("Received null server data, ignoring...");
                    } else {
                        logger.log("Forwarding client data to lobby data queue...");
                        serverDataQueue.offer(serverData);
                    }
                } catch (NetworkFailureException e) {
                    logger.log("Failed to read data from input stream", e);
                }
            }
        } catch (EOFException e) {
            logger.log("Connection closed by server");
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
        logger.log("Client dataThread closed");
    }

    public void send(AbstractClientData message) throws IOException {
        if (!running) {
            logger.log("Client is closed, cannot send message.");
            return;
        }

        out.writeObject(message);
        out.flush();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void close() throws IOException, InterruptedException {
        if (!running) {
            logger.log("Client is already closed.");
            return;
        }

        running = false;
        socket.close();
        logger.log("Client closed");
    }
}
