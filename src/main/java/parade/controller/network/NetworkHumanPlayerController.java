package parade.controller.network;

import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.exceptions.PlayerControllerInitialisationException;
import parade.common.state.client.AbstractClientData;
import parade.common.state.client.ClientCardPlayData;
import parade.common.state.client.ClientConnectData;
import parade.common.state.client.ClientPoisonPill;
import parade.common.state.server.*;
import parade.logger.LoggerProvider;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NetworkHumanPlayerController implements INetworkPlayerController, Closeable {
    private final Player player;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private volatile boolean running = true;
    private final BlockingQueue<AbstractClientData> clientDataQueue = new LinkedBlockingQueue<>();

    public NetworkHumanPlayerController(Socket socket)
            throws IOException, NetworkFailureException, PlayerControllerInitialisationException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());

        this.player = initialHandShake();

        Thread.ofVirtual().start(this::listenForClientData);
        Thread.ofVirtual().start(this::processData);

        LoggerProvider.getInstance()
                .log("NetworkHumanPlayerController created for player: " + this.player.getName());
    }

    private Player initialHandShake()
            throws IOException, NetworkFailureException, PlayerControllerInitialisationException {
        try {
            readSingleInput(); // this sets a listener to listen for single input
        } catch (NetworkFailureException | IOException e) {
            // Failed to receive the data we need
            LoggerProvider.getInstance().log("Failed to read connect data from input stream", e);
            close();
        }
        AbstractClientData data;
        try {
            data = clientDataQueue.poll(10_000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Thread was interrupted while waiting for data
            LoggerProvider.getInstance().log("Thread interrupted while waiting for data", e);
            close();
            throw new PlayerControllerInitialisationException(
                    "clientDataQueue thread interrupted while waiting for data", e);
        }
        if (data == null) {
            LoggerProvider.getInstance()
                    .log("Failed to receive connect data from client, closing connection.");
            send(new ServerConnectAckData(false, "Timeout waiting for connect data"));
            this.socket.close();
            throw new PlayerControllerInitialisationException(
                    "Failed to receive connect data from client");
        }
        if (!(data instanceof ClientConnectData connectData)) {
            LoggerProvider.getInstance()
                    .log("Invalid connect data, data type mismatch. Closing connection.");
            send(new ServerConnectAckData(false, "Invalid connect data"));
            this.socket.close();
            throw new PlayerControllerInitialisationException(
                    "Incoming data is not of type ClientConnectData");
        }

        Player caller = connectData.getCaller();
        if (caller == null || caller.getName() == null || caller.getName().isEmpty()) {
            LoggerProvider.getInstance()
                    .log("Invalid connect data, player name is invalid. Closing connection.");
            send(new ServerConnectAckData(false, "Invalid connect data"));
            this.socket.close();
            throw new PlayerControllerInitialisationException("Invalid player name");
        }

        LoggerProvider.getInstance()
                .logf("Initial handshake successful, player name: %s", caller.getName());
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
    public void handle(AbstractClientData clientData) throws NetworkFailureException {
        switch (clientData) {
            case ClientConnectData connectData -> {}
            case ClientCardPlayData cardPlayData -> {}
            case ClientPoisonPill poisonPill -> {
                // This is to ensure that when the client is closed, the other thread is
                // notified and can stop waiting for data
                LoggerProvider.getInstance().log("Received poison pill, ignoring...");
            }
            default -> {
                LoggerProvider.getInstance()
                        .log("Unsupported client data type: " + clientData.getClientAction());
                //                    throw new NetworkFailureException(
                //                            "Unknown client data type: " +
                // clientData.getClass().getSimpleName());
            }
        }
    }

    private void readSingleInput() throws NetworkFailureException, IOException {
        try {
            Object inputObject = in.readObject();
            if (inputObject instanceof AbstractClientData clientData) {
                clientDataQueue.offer(clientData);
                LoggerProvider.getInstance()
                        .log("Received client data type: " + clientData.getClientAction());
            } else {
                throw new NetworkFailureException(
                        "Server received data is not of type AbstractClientData");
            }
        } catch (ClassNotFoundException e) {
            throw new NetworkFailureException("Invalid class for object input", e);
        }
    }

    public void listenForClientData() {
        LoggerProvider.getInstance()
                .logf("NetworkHumanPlayerController (%s) listening for data", player.getName());
        try {
            while (running) {
                try {
                    readSingleInput();
                } catch (NetworkFailureException e) {
                    LoggerProvider.getInstance().log("Failed to read data from input stream", e);
                }
            }
        } catch (EOFException e) {
            LoggerProvider.getInstance().log("Connection closed by client", e);
        } catch (IOException e) {
            LoggerProvider.getInstance().log("I/O error occurred", e);
        } catch (Exception e) {
            LoggerProvider.getInstance().log("Unexpected error occurred", e);
        } finally {
            try {
                close();
            } catch (IOException e) {
                LoggerProvider.getInstance().log("Error closing connection", e);
            }
        }
    }

    public void processData() {
        LoggerProvider.getInstance()
                .logf("NetworkHumanPlayerController (%s) ready to process data", player.getName());
        try {
            while (running) {
                AbstractClientData clientData = clientDataQueue.take();
                LoggerProvider.getInstance()
                        .log("Processing client data type: " + clientData.getClientAction());
                handle(clientData);
            }
        } catch (NetworkFailureException | InterruptedException e) {
            LoggerProvider.getInstance().log("Error processing client data", e);
        } catch (Exception e) {
            LoggerProvider.getInstance().log("Unexpected error occurred", e);
        } finally {
            try {
                close();
            } catch (IOException e) {
                LoggerProvider.getInstance().log("Error closing connection", e);
            }
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void close() throws IOException {
        if (!running) {
            LoggerProvider.getInstance().log("Controller is already closed");
            return;
        }
        running = false;
        socket.close();
        clientDataQueue.offer(new ClientPoisonPill());
    }

    //    public void handle(AbstractServerData data) throws NetworkFailureException {
    //        switch (data) {
    //            case ServerConnectAckData ackData -> System.out.println(ackData);
    //            case ServerGameEndData endData -> System.out.println(endData);
    //            case ServerGameStartData startData -> System.out.println(startData);
    //            case ServerGameFinalRoundData finalRoundData ->
    // System.out.println(finalRoundData);
    //            case ServerPlayerDrawnCardData drawnCardData ->
    // cards.add(drawnCardData.getCard());
    //            case ServerPlayerTurnData turnData -> System.out.println(turnData);
    //            case ServerPlayerReceivedParadeCardsData receivedParadeCardsData ->
    //                    System.out.println(receivedParadeCardsData);
    //            case ServerLobbyClosedData closedData -> System.out.println(closedData);
    //            case ServerLobbyCreateAckData createAckData -> System.out.println(createAckData);
    //            case ServerLobbyJoinAckData joinAckData -> System.out.println(joinAckData);
    //            case ServerLobbyListData lobbyListData -> System.out.println(lobbyListData);
    //            case ServerLobbyPlayerJoinedData playerJoinedData ->
    //                    System.out.println(playerJoinedData);
    //            case ServerLobbyPlayerLeftData playerLeftData ->
    // System.out.println(playerLeftData);
    //            case null -> System.out.println("Received null data");
    //            default -> System.out.println("Unknown data type: " +
    // data.getClass().getSimpleName());
    //        }
    //    }

}
