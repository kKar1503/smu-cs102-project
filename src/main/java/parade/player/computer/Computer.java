package parade.player.computer;

import java.util.LinkedList;
import java.util.List;

import parade.common.Card;
import parade.player.IPlayer;

/**
 * The Computer class is an abstract representation of an AI player in the game.
 * It contains common functionality shared among different AI difficulty levels.
 */
public abstract class Computer implements IPlayer {
    protected LinkedList<Card> hand; // Represents the player's current hand.
    protected LinkedList<Card> board; // Represents the cards collected from the parade.
    protected String name;            // Stores the name of the AI player.

    /**
     * Constructs a Computer player with a given name and initial hand.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     * @param name  The name of the AI player.
     */
    public Computer(List<Card> cards, String name) {
        this.hand = new LinkedList<>(cards);
        this.board = new LinkedList<>();
        this.name = name;
    }

    /**
     * Adds multiple cards to the AI player's board (collected cards).
     *
     * @param cards The list of cards to add to the board.
     */
    @Override
    public void addToBoard(List<Card> cards) {
        board.addAll(cards); // Appends all cards from the list to the board.
    }

    /**
     * Draws a new card and adds it to the AI player's hand.
     *
     * @param card The card drawn from the deck.
     */
    @Override
    public void draw(Card card) {
        hand.add(card);
    }

    /**
     * Retrieves the AI player's current hand.
     *
     * @return A list of cards in the AI player's hand.
     */
    @Override
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Retrieves the cards that the AI player has collected from the parade.
     *
     * @return A list of cards representing the AI player's board.
     */
    @Override
    public List<Card> getBoard() {
        return board;
    }

    /**
     * Retrieves the name of the AI player.
     *
     * @return The name of the AI player.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the name of computer player
     * 
     * @param input The desired name of the computer player
     */
    public void setName(String input) {
        name = input;
    }
}
