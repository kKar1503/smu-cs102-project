package parade.player;

import parade.common.Card;

import java.util.List;

public interface Player {
    /**
     * Chooses a card to play based on the current state of the parade.
     *
     * @param parade The list of cards currently in the parade.
     * @return The card selected to be played.
     */
    Card playCard(List<Card> parade);

    /**
     * Draws a new card and adds it to the player's hand.
     *
     * @param card The card drawn from the deck.
     */
    void draw(Card card);

    /**
     * Retrieves the player's current hand.
     *
     * @return A list of cards the player is holding.
     */
    List<Card> getHand();

    /**
     * Retrieves the cards that the player has collected from the parade. Represents the player's
     * current board state.
     *
     * @return A list of cards representing the player's board.
     */
    List<Card> getBoard();

    /**
     * Retrieves the player's name.
     * This helps differentiate between different types of players (human or AI).
     *
     * @return The player's name as a string.
     */
    String getName();
}
