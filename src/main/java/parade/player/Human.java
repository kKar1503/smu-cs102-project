package parade.player;

import parade.common.Card;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * The Human class represents a human player in the game. It implements the Player interface and
 * provides functionality for a human player such as drawing and playing cards.
 */
public class Human implements Player {

    private String name;
    private LinkedList<Card> hand;
    private LinkedList<Card> board;

    /**
     * Constructs a human player with a given name and initial hand.
     *
     * @param cards The initial set of cards assigned to the human player's hand.
     * @param name The name of the human player.
     */
    public Human(LinkedList<Card> cards, String name) {
        this.name = name;
        this.hand = new LinkedList<>(cards);
        this.board = new LinkedList<>();
    }

    /**
     * Allows player to choose a card to play from their hand
     *
     * @param parade The current lineup of cards in the parade.
     * @return The card chosen by the player.
     */
    public Card playCard(LinkedList<Card> parade) {

        Scanner sc = new Scanner(System.in);
        int input;

        do {
            try {
                input = sc.nextInt();
                break;

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please try again");
                System.out.println("Select a card to play:");

            } catch (IndexOutOfBoundsException e) {
                System.out.println("Invalid choice. Please select a valid index");
                System.out.println("Select a card to play:");
            }
        } while (true);

        return hand.remove(input);
    }

    /**
     * Adds multiple cards to the human player's board (collected cards).
     *
     * @param cards The list of cards to add to the board.
     */
    @Override
    public void addToBoard(List<Card> cards) {
        board.addAll(cards); // Appends all cards from the list to the board.
    }

    /** Adds a drawn card to the player's hand */
    public void draw(Card card) {
        hand.add(card);
    }

    /**
     * Retrieves the human player's current hand.
     *
     * @return A list of cards in the human player's hand.
     */
    @Override
    public LinkedList<Card> getHand() {
        return hand;
    }

    /**
     * Retrieves the cards that the human player has collected from the parade.
     *
     * @return A list of cards representing the human player's board.
     */
    @Override
    public LinkedList<Card> getBoard() {
        return board;
    }

    /**
     * Retrieves the human player's name.
     *
     * @return The human player's name as a string.
     */
    public String getName() {
        return name;
    }
}
