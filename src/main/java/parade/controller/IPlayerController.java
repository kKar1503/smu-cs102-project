package parade.controller;

import parade.common.Card;
import parade.common.Player;
import parade.common.state.server.PlayerTurnData;

/**
 * The IPlayerController interface defines the contract that a player controller should hold for the
 * local game engine. The local game engine uses this interface to query for the player's actions.
 */
public interface IPlayerController {
    /**
     * Chooses a card to play based on the current state of the parade.
     *
     * @param playerTurnData data object that contains sufficient information for the player to make
     *     a decision for their turn.
     * @return The card selected to be played.
     */
    Card playCard(PlayerTurnData playerTurnData);

    Player getPlayer();
}
