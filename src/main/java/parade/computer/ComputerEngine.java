package parade.computer;

import parade.card.Card;
import parade.player.Player;
import parade.player.controller.PlayCardData;

/**
 * The ComputerEngine interface is an interface for all computer engines built to interact with the
 * game.
 *
 * <p>The impls of ComputerEngine should be stateless and relies on the process method that allows
 * it to consume all required information to process the game state and return the ideal card to
 * play.
 */
public interface ComputerEngine {
    /**
     * Process the game state and return the ideal card to play.
     *
     * <p>The computer engine must not be modifying the game state, such as removing any cards from
     * the player's hand or the parade. Its decision should be solely on returning which card it
     * thinks should be played.
     *
     * <p>The computer engine should accomplish this via player.getHand().get(index), where index is
     * the index of the card in the player's hand. The implementations of player controller should
     * attempt to restrict from passing a modifiable list of cards to the computer engine and should
     * disable the .removeFromHand() method in the player passed to the computer engine.
     *
     * @param player The player that this computer is playing for.
     * @param playCardData The game state that the computer engine can use to make its decision.
     * @return The ideal card to play.
     */
    Card process(Player player, PlayCardData playCardData);

    Card discardCard(Player player, PlayCardData playCardData);

    /**
     * Get the name of the engine.
     *
     * @return The name of the engine.
     */
    String getName();
}
