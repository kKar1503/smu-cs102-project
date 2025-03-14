package parade.controller.local;

import parade.common.Card;
import parade.common.Player;
import parade.controller.IPlayer;
import parade.renderer.local.ClientRendererProvider;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The Human class represents a human player in the game. It implements the Player interface and
 * provides functionality for a human player such as drawing and playing cards.
 */
public class LocalHumanController implements IPlayer {
    private final Player player;
    private Card latestDrawnCard;

    /**
     * Constructs a human player with a given name and initial hand.
     *
     * @param player The human player.
     */
    public LocalHumanController(Player player) {
        this.player = player;
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
        ClientRendererProvider.getInstance().renderPlayerTurn(player, latestDrawnCard, parade);

        while (true) {
            try {
                input = sc.nextInt();
                if (input < 1 || input > player.getHand().size()) {
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
        return player.removeFromHand(input - 1);
    }

    /**
     * Gets the underlying player that the LocalHumanController is controlling.
     *
     * @return The underlying Player object.
     */
    public Player getPlayer() {
        return player;
    }
}
