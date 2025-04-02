package parade.controller.network.client;

import parade.common.state.client.AbstractClientData;
import parade.controller.IPlayerController;

import java.io.Closeable;
import java.io.IOException;

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
public interface IClientPlayerController extends IPlayerController, Closeable {
    /**
     * Allows the client to send data to the server. This method is called when the client has
     * action or information to inform the server, such as what card they want to play or any
     * actions related to the lobby.
     *
     * @param clientData an {@link AbstractClientData} object which contains information from the
     *     client to inform the server of client action
     */
    void send(AbstractClientData clientData) throws IOException;
}
