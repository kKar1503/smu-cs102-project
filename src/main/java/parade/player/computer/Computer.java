package parade.player.computer;

import parade.common.Card;
import parade.player.Player;

import java.util.LinkedList;
import java.util.List;

public abstract class Computer implements Player {
    protected LinkedList<Card> hand; // Represents the player's current hand.
    protected LinkedList<Card> board; // Represents the cards collected from the parade.

    /**
     * Constructor for the Computer player. Initializes the hand and an empty board.
     *
     * @param cards The initial set of cards assigned to the computer's hand.
     */
    public Computer(List<Card> cards) {
        this.hand = new LinkedList<>(cards);
        this.board = new LinkedList<>();
    }

    @Override
    public void draw(Card card) {
        hand.add(card); // Adds a drawn card to the hand.
    }

    @Override
    public List<Card> getHand() {
        return hand; // Returns the list of cards in hand.
    }

    @Override
    public List<Card> getBoard() {
        return board; // Returns the list of collected cards.
    }
}
