package parade.network.client;

import parade.common.Player;
import parade.common.exceptions.NetworkFailureException;
import parade.common.state.server.AbstractServerData;
import parade.controller.network.client.ClientHumanPlayerController;
import parade.controller.network.client.IClientPlayerController;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements AutoCloseable {
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();

    private final int port;
    private final String host;
    private IClientPlayerController clientPlayerController;
    private volatile boolean running = false;

    private final BlockingQueue<AbstractServerData> dataQueue = new LinkedBlockingQueue<>();

    private Thread thread;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException, NetworkFailureException {
        if (thread.isAlive() || running) {
            LOGGER.log("Server is already running");
            return;
        }

        running = true;

        // Prompts for user
        Player player = new Player("Kar");

        Socket socket = new Socket(host, port);
        clientPlayerController = new ClientHumanPlayerController(socket, player, dataQueue);
        LOGGER.log("Client started and connected to the server");
    }

    @Override
    public void close() throws Exception {
        if (!running) {
            LOGGER.log("Client is already closed");
            return;
        }

        running = false;
        clientPlayerController.close();
        LOGGER.log("Server closed");

        thread.join(5_000);

        if (thread.isAlive()) {
            LOGGER.log("Thread did not terminate in time");
            thread.interrupt();
        } else {
            LOGGER.log("Thread terminated");
        }

        LOGGER.log("Client closed successfully");
    }
}
