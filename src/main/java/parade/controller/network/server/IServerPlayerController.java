package parade.controller.network.server;

import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;
import parade.controller.IPlayerController;

import java.io.Closeable;
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
public interface IServerPlayerController extends IPlayerController, Closeable {
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
     * Allows the game engine to receive data from the player. This method is called when the game
     * engine takes over the PlayerController from the server.
     *
     * @param recvDataQueue a {@link BlockingQueue} of {@link AbstractClientData} objects which
     *     contains information from the player to be processed by the game engine.
     */
    void setRecvDataQueue(BlockingQueue<AbstractClientData> recvDataQueue);
}
