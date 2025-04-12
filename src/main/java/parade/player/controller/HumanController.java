package parade.player.controller;

import parade.card.Card;
import parade.menu.manager.MenuManager;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The Human class represents a human player in the game. It implements the Player interface and
 * provides functionality for a human player such as drawing and playing cards.
 */
public class HumanController extends AbstractPlayerController {
    private Card latestDrawnCard;
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
        Scanner sc = new Scanner(System.in);
        int input;
        menuManager.renderPlayerTurn(player, latestDrawnCard, playCardData, false);

        while (true) {
            try {
                input = sc.nextInt();
                if (input < 1 || input > player.getHand().size()) {
                    throw new IndexOutOfBoundsException();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please try again");
            } catch (IndexOutOfBoundsException e) {
                System.out.print("Invalid choice. Please select a valid index");
            }
            System.out.print("Select a card to play:");
            sc.nextLine();
        }
        latestDrawnCard = null;
        return player.removeFromHand(input - 1);
    }

    @Override
    public Card discardCard(PlayCardData playCardData) {
        Scanner sc = new Scanner(System.in);
        int input;
        menuManager.renderPlayerTurn(player, null, playCardData, true);

        while (true) {
            try {
                input = sc.nextInt();
                if (input < 1 || input > player.getHand().size()) {
                    throw new IndexOutOfBoundsException();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please try again");
            } catch (IndexOutOfBoundsException e) {
                System.out.print("Invalid choice. Please select a valid index");
            }
            System.out.print("Select a card to discard:");
            sc.nextLine();
        }
        return player.removeFromHand(input - 1);
    }
}
