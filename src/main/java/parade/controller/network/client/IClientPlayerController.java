package parade.controller.network.client;

import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;
import parade.controller.IPlayerController;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * The IClientPlayerController interface defines the contract that a player controller should hold
 * for a network game engine for the client application.
 *
 * <p>The IClientPlayerController interface is less focused on being "network" but rather an
 * asynchronous player controller interface. The difference from the ILocalPlayerController is that
 * the return of the send method is not expected to be immediate. The player controller can send
 * data to the server and without expecting any response. The response will later come in whenever
 * the server sends the data back to the client. This data when received, then will be processed by
 * the client and the player interactions.
 */
public interface IClientPlayerController extends IPlayerController, AutoCloseable {
    /**
     * Allows the client to send data to the server. This method is called when the client has
     * action or information to inform the server, such as what card they want to play or any
     * actions related to the lobby.
     *
     * @param clientData an {@link AbstractClientData} object which contains information from the
     *     client to inform the server of client action
     */
    void send(AbstractClientData clientData) throws IOException;

    /**
     * Set server data queue of the client. This method assigns a queue to the player controller for
     * transmitting all the data from the socket to the client. The queue is used to deliver the
     * data from the server.
     *
     * @param recvDataQueue the queue to be set for the server data
     */
    void setRecvDataQueue(BlockingQueue<AbstractServerData> recvDataQueue);

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
