package parade.controller.network.server;

import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.exceptions.PlayerControllerInitialisationException;
import parade.common.state.client.*;
import parade.common.state.server.*;
import parade.controller.network.AbstractNetworkController;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ServerHumanPlayerController
        extends AbstractNetworkController<AbstractServerData, AbstractClientData>
        implements IServerPlayerController {
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();

    private final Player player;

    public ServerHumanPlayerController(Socket socket, BlockingQueue<AbstractClientData> lobbyQueue)
            throws IOException, NetworkFailureException, PlayerControllerInitialisationException {
        super(socket, lobbyQueue, AbstractClientData.class);

        this.player = initialHandShake();
        super.start();
        LOGGER.log("ServerHumanPlayerController created for player: " + this.player.getName());
    }

    private Player initialHandShake() throws IOException, PlayerControllerInitialisationException {
        AbstractClientData data;
        try {
            data = readSingleInput(10_000);
        } catch (NetworkFailureException | IOException e) {
            // Failed to receive handshake data
            LOGGER.log("Failed to receive connect data", e);
            close();
            throw new PlayerControllerInitialisationException("Failed to receive connect data", e);
        }
        if (data == null) {
            LOGGER.log("Timed out waiting for connect data");
            send(new ServerConnectAckData(false, "Timed out waited 10,000ms"));
            close();
            throw new PlayerControllerInitialisationException("Timed out receiving connect data");
        }
        if (!(data instanceof ClientConnectData connectData)) {
            LOGGER.log("Invalid connect data, data type mismatch. Closing connection.");
            send(new ServerConnectAckData(false, "Invalid connect data"));
            close();
            throw new PlayerControllerInitialisationException(
                    "Incoming data is not of type ClientConnectData");
        }

        Player caller = connectData.getCaller();
        if (caller == null || caller.getName() == null || caller.getName().isEmpty()) {
            LOGGER.log("Invalid connect data, player name is invalid. Closing connection.");
            send(new ServerConnectAckData(false, "Invalid connect data"));
            close();
            throw new PlayerControllerInitialisationException("Invalid player name");
        }

        LOGGER.logf("Initial handshake successful, player name: %s", caller.getName());
        send(new ServerConnectAckData(true, "Connected to the server! " + caller.getName()));
        return caller;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void close() throws IOException {
        super.close();
        LOGGER.logf("ServerHumanPlayerController (%s) closed", player.getName());
    }

    @Override
    public String toString() {
        return "ServerHumanPlayerController{player=" + player + ", running=" + running + '}';
    }
}
