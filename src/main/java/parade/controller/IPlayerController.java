package parade.controller;

import parade.common.Player;

/**
 * The IPlayerController interface defines the contract that a player controller should hold for the
 * game engine.
 */
public interface IPlayerController {
    /**
     * The player controller should hold a reference to the underlying {@link Player} object, which
     * represents the player in the game.
     *
     * @return The {@link Player} associated with this controller.
     */
    Player getPlayer();
}
