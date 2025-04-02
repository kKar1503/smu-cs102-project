package parade.controller.network.client;

import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.state.client.AbstractClientData;
import parade.common.state.client.ClientConnectData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerConnectAckData;
import parade.controller.network.AbstractNetworkController;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.IOException;
import java.net.Socket;

public class ClientHumanPlayerController
        extends AbstractNetworkController<AbstractClientData, AbstractServerData>
        implements IClientPlayerController, AutoCloseable {
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();

    private final Player player;

    public ClientHumanPlayerController(Socket socket, Player player)
            throws IOException, NetworkFailureException {
        super(socket, AbstractServerData.class);
        this.player = player;

        initiateHandshake();
        super.start();
        LOGGER.log("ServerHumanPlayerController created for player: " + this.player.getName());
    }

    private void initiateHandshake() throws IOException, NetworkFailureException {
        LOGGER.log("Initiating handshake with server...");
        send(new ClientConnectData(player));
        AbstractServerData data;
        try {
            data = readSingleInput(10_000);
        } catch (NetworkFailureException | IOException e) {
            LOGGER.log("Failed to receive connection ack data", e);
            close();
            throw new NetworkFailureException("Failed to receive connection ack data", e);
        }
        if (data == null) {
            LOGGER.log("Timed out waiting for connection ack data");
            close();
            throw new NetworkFailureException("Timed out waiting for connection ack data");
        }
        if (!(data instanceof ServerConnectAckData connectAckData)) {
            LOGGER.log("Received unexpected data type during handshake: " + data);
            close();
            throw new NetworkFailureException("Received unexpected data type during handshake");
        }
        if (!connectAckData.isAccepted()) {
            LOGGER.log("Handshake failed: " + connectAckData.getMessage());
            close();
            throw new NetworkFailureException("Handshake failed: " + connectAckData.getMessage());
        }

        LOGGER.log("Handshake successful: " + connectAckData.getMessage());
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void close() throws IOException {
        super.close();
        LOGGER.logf("ClientHumanPlayerController (%s) closed", player.getName());
    }

    @Override
    public String toString() {
        return "ClientHumanPlayerController{player=" + player + ", running=" + running + '}';
    }
}
