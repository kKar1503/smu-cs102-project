package parade.controller.network;

import parade.common.exceptions.NetworkFailureException;
import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;
import parade.controller.IPlayerController;

/**
 * The INetworkPlayerController interface defines the contract that a player controller should hold
 * for a network game engine.
 *
 * <p>The INetworkPlayerController interface is less focused on being "network" but rather an
 * asynchronous player controller interface. The difference from the ILocalPlayerController is that
 * the return of the send method is not expected to be immediate. The player controller can send
 * data to the client and without expecting any response. The response will later come in whenever
 * the client sends the data back to the server. This data when received, then will be processed by
 * the server using the handle method.
 */
public interface INetworkPlayerController extends IPlayerController {
    /**
     * Allows the server to send data to the player. This method is called when the server has
     * information to share with the player, such as the current game state or other players'
     * actions.
     *
     * @param serverData an {@link AbstractServerData} object which contains information from the
     *     server for the player to act.
     */
    void send(AbstractServerData serverData) throws NetworkFailureException;

    /**
     * Handles the incoming data from the client. This method is called when the player controller
     * receives data from the client, such as the player's action or other relevant information.
     *
     * @param clientData an {@link AbstractClientData} object which contains sufficient information
     *     for the server to act.
     */
    void handle(AbstractClientData clientData) throws NetworkFailureException;
}
