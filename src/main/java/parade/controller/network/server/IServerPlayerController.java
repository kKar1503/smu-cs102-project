package parade.controller.network.server;

import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;
import parade.controller.IPlayerController;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * The IServerPlayerController interface defines the contract that a player controller should hold
 * for a network game engine.
 *
 * <p>The IServerPlayerController interface is less focused on being "network" but rather an
 * asynchronous player controller interface. The difference from the ILocalPlayerController is that
 * the return of the send method is not expected to be immediate. The player controller can send
 * data to the client and without expecting any response. The response will later come in whenever
 * the client sends the data back to the server. This data when received, then will be processed by
 * the server's game engine.
 */
public interface IServerPlayerController extends IPlayerController, AutoCloseable {
    /**
     * Allows the server to send data to the player. This method is called when the server has
     * information to share with the player, such as the current game state or other players'
     * actions.
     *
     * @param serverData an {@link AbstractServerData} object which contains information from the
     *     server for the player to act.
     */
    void send(AbstractServerData serverData) throws IOException;

    /**
     * Set client data queue of the lobby. This method assigns a queue to the player controller for
     * transmitting all the data from the client socket to the server. The queue is used to deliver
     * the data to the server.
     *
     * @param recvDataQueue the queue to be set for the client data
     */
    void setRecvDataQueue(BlockingQueue<AbstractClientData> recvDataQueue);

    /**
     * Closes the player controller. This method is called when the player controller is no longer
     * needed, such as when the game ends or the player disconnects.
     *
     * <p>This method should stop the controller from listening for incoming data and close any
     * resources associated with the player controller, such as sockets or streams. This method also
     * tells the controller to stop sending data to the server with the {@link BlockingQueue} set in
     * the {@link #setRecvDataQueue(BlockingQueue)} method.
     *
     * @throws Exception if an error occurs while closing the player controller
     */
    @Override
    void close() throws Exception;
}
