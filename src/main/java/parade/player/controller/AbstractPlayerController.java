package parade.player.controller;

import parade.card.Card;
import parade.player.Player;

/**
 * The AbstractPlayerController abstract class defines the default behaviour that a player
 * controller should hold for the game engine.
 */
public abstract class AbstractPlayerController {
    final Player player;

    AbstractPlayerController(String name) {
        this.player = new Player(name);
    }

    /**
     * Draw cards to the player's hand. This method is a delegate to the {@link
     * Player#addToHand(Card...)} method.
     *
     * @param cards the cards drawn for the player.
     */
    public void draw(Card... cards) {
        player.addToHand(cards);
    }

    /**
     * Place card onto the board, which are usually cards drawn from the parade. This method is a
     * delegate to the {@link Player#addToBoard(Card...)} method.
     *
     * @param cards the cards the player receives from the parade.
     */
    public void receiveFromParade(Card... cards) {
        player.addToBoard(cards);
    }

    /**
     * Play a card from the player's hand.
     *
     * <p>This method should remove the card to play from the player's hand.
     *
     * @param playCardData The data object that contains the information for the player to act upon
     *     their turn.
     * @return The card that the player wants to play
     */
    public abstract Card playCard(PlayCardData playCardData);

    /**
     * The player controller holds a reference to the underlying {@link Player} object, which
     * represents the player in the game.
     *
     * @return The {@link Player} associated with this controller.
     */
    public Player getPlayer() {
        return player;
    }
}
