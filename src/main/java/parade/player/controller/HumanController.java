package parade.player.controller;

import parade.card.Card;
import parade.menu.manager.MenuManager;

/**
 * The Human class represents a human player in the game. It implements the Player interface and
 * provides functionality for a human player such as drawing and playing cards.
 */
public class HumanController extends AbstractPlayerController {
    private final MenuManager menuManager;

    /**
     * Constructs a human player with a given name and initial hand.
     *
     * @param name The name for the human player.
     */
    public HumanController(String name, MenuManager menuManager) {
        super(name);
        this.menuManager = menuManager;
    }

    /**
     * Allows local human player to choose a card to play from their hand
     *
     * @param playCardData The current lineup of cards in the parade.
     * @return The card chosen by the player using the {@link PlayCardData} client data object.
     */
    @Override
    public Card playCard(PlayCardData playCardData) {
        int cardIndexToPlay = menuManager.playerTurnMenu(player, playCardData, false);
        return player.removeFromHand(cardIndexToPlay);
    }

    @Override
    public Card discardCard(PlayCardData playCardData) {
        int cardIndexToRemove = menuManager.playerTurnMenu(player, playCardData, true);
        return player.removeFromHand(cardIndexToRemove);
    }
}
