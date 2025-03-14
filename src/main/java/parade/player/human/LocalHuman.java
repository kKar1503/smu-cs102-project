package parade.player.human;

import parade.common.Card;
import parade.player.IPlayer;
import parade.renderer.ClientRendererProvider;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * The Human class represents a human player in the game. It implements the Player interface and
 * provides functionality for a human player such as drawing and playing cards.
 */
public class LocalHuman implements IPlayer {

    private final String name;
    private final LinkedList<Card> hand;
    private final LinkedList<Card> board;
    private Card latestDrawnCard;

    /**
     * Constructs a human player with a given name and initial hand.
     *
     * @param name The name of the human player.
     */
    public LocalHuman(String name) {
        this.name = name;
        this.hand = new LinkedList<>();
        this.board = new LinkedList<>();
    }

    /**
     * Allows player to choose a card to play from their hand
     *
     * @param parade The current lineup of cards in the parade.
     * @return The card chosen by the player.
     */
    public Card playCard(List<Card> parade) {

        Scanner sc = new Scanner(System.in);
        int input;
        ClientRendererProvider.getInstance().renderPlayerTurn(this, latestDrawnCard, parade);

        while (true) {
            try {
                input = sc.nextInt();
                if (input < 1 || input > hand.size()) {
                    throw new IndexOutOfBoundsException();
                }
                break;
            } catch (InputMismatchException e) {
                ClientRendererProvider.getInstance().render("Invalid input. Please try again");
            } catch (IndexOutOfBoundsException e) {
                ClientRendererProvider.getInstance()
                        .render("Invalid choice. Please select a valid index");
            }
            ClientRendererProvider.getInstance().render("Select a card to play:");
            sc.nextLine();
        }
        latestDrawnCard = null;
        return hand.remove(input - 1);
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
    @Override
    public void draw(Card card) {
        latestDrawnCard = card;
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
