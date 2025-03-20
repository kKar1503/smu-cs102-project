package parade.controller.local;

import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerPlayerDrawnCardData;
import parade.controller.IPlayerController;

/**
 * The ILocalPlayerController interface defines the contract that a player controller should hold
 * for a local game engine.
 *
 * <p>The ILocalPlayerController interface is less of focused on being "local" but rather focused on
 * being a synchronous interface. Technically we can build a network player controller that acts
 * synchronously by doing some sort of pub-sub structure through the TCP socket.
 *
 * <p>But right now, this implementation will use for local players only.
 *
 * <p>Maybe when there's time, we can build a synchronous network player controller that uses the
 * same interface.
 */
public interface ILocalPlayerController extends IPlayerController {
    /**
     * Allows the server to send data to the player. This method is called when the server has
     * information to share with the player, such as the current game state or other players'
     * actions.
     *
     * <p>This method is used synchronously where the player action is expected to immediately come
     * back when the method is called.
     *
     * <p>This method can sometimes be expected to return {@code null} if the player might not need
     * to or unable to act on the server data. For example, if the server deals a card to the client
     * using the {@link ServerPlayerDrawnCardData}, the client shouldn't be expected to return any
     * data. Generally, in this case, even if the player returns any data, the server will ignore
     * it.
     *
     * @param serverData an {@link AbstractServerData} object which contains information for the
     *     player to act.
     * @return an {@link AbstractClientData} object which contains sufficient information for the
     *     server to act.
     */
    AbstractClientData send(AbstractServerData serverData);
}
