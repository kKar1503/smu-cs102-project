package parade.player.controller;

import parade.card.Card;
import parade.renderer.local.ClientRendererProvider;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The Human class represents a human player in the game. It implements the Player interface and
 * provides functionality for a human player such as drawing and playing cards.
 */
public class HumanController extends AbstractPlayerController {
    private Card latestDrawnCard;

    /**
     * Constructs a human player with a given name and initial hand.
     *
     * @param name The name for the human player.
     */
    public HumanController(String name) {
        super(name);
    }

    /**
     * Allows local human player to choose a card to play from their hand
     *
     * @param playCardData The current lineup of cards in the parade.
     * @return The card chosen by the player using the {@link PlayCardData} client data object.
     */
    @Override
    public Card playCard(PlayCardData playCardData) {
        Scanner sc = new Scanner(System.in);
        int input;
    
        ClientRendererProvider.getInstance()
                .renderPlayerTurn(player, latestDrawnCard, playCardData, false);
    
        while (true) {
            try {
                input = sc.nextInt();
                if (input < 1 || input > player.getHand().size()) {
                    throw new IndexOutOfBoundsException();
                }
                break; // Valid input received
            } catch (InputMismatchException e) {
                ClientRendererProvider.getInstance()
                        .renderln(System.lineSeparator() + "Invalid input. Please enter a number.");
            } catch (IndexOutOfBoundsException e) {
                ClientRendererProvider.getInstance()
                        .renderln(System.lineSeparator() + "Invalid choice. Please select a valid index.");
            }
    
            ClientRendererProvider.getInstance()
                    .render("Select a card to play:" + System.lineSeparator());
            sc.nextLine(); // Clear the scanner buffer
        }
    
        latestDrawnCard = null;
        return player.removeFromHand(input - 1);
    }

    @Override
    public Card discardCard(PlayCardData playCardData) {
        Scanner sc = new Scanner(System.in);
        int input;

        // Render the player's turn with discard mode enabled
        ClientRendererProvider.getInstance().renderPlayerTurn(player, null, playCardData, true);

        while (true) {
            try {
                input = sc.nextInt();

                if (input < 1 || input > player.getHand().size()) {
                    throw new IndexOutOfBoundsException();
                }
                break; // Valid input
            } catch (InputMismatchException e) {
                ClientRendererProvider.getInstance().renderln(System.lineSeparator() + "Invalid input. Please enter a number.");
            } catch (IndexOutOfBoundsException e) {
                ClientRendererProvider.getInstance().renderln(System.lineSeparator() + "Invalid choice. Please select a valid index.");
            }

            // Prompt again on a new line
            ClientRendererProvider.getInstance().render("Select a card to discard:" + System.lineSeparator());
            sc.nextLine(); // Clear scanner buffer
        }

        return player.removeFromHand(input - 1);
    }
}
