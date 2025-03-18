package parade.controller.local.human;

import parade.common.Card;
import parade.common.Player;
import parade.common.state.client.AbstractClientData;
import parade.common.state.client.ClientCardPlayData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerPlayerDrawnCardData;
import parade.common.state.server.ServerPlayerReceivedParadeCardsData;
import parade.common.state.server.ServerPlayerTurnData;
import parade.controller.local.ILocalPlayerController;
import parade.renderer.local.ClientRendererProvider;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The Human class represents a human player in the game. It implements the Player interface and
 * provides functionality for a human player such as drawing and playing cards.
 */
public class LocalHumanController implements ILocalPlayerController {
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

    @Override
    public AbstractClientData send(AbstractServerData serverData) {
        return switch (serverData) {
            case ServerPlayerTurnData playerTurnData -> playCard(playerTurnData);
            case ServerPlayerDrawnCardData playerDrawnCardData ->
                    addToPlayerHand(playerDrawnCardData);
            case ServerPlayerReceivedParadeCardsData playerReceivedParadeCardsData ->
                    addToPlayerBoard(playerReceivedParadeCardsData);
            default ->
                    throw new UnsupportedOperationException(
                            "Unsupported server data for this player controller");
        };
    }

    /**
     * Allows local human player to choose a card to play from their hand
     *
     * @param playerTurnData The current lineup of cards in the parade.
     * @return The card chosen by the player using the {@link ClientCardPlayData} client data
     *     object.
     */
    private ClientCardPlayData playCard(ServerPlayerTurnData playerTurnData) {
        Scanner sc = new Scanner(System.in);
        int input;
        ClientRendererProvider.getInstance()
                .renderPlayerTurn(player, latestDrawnCard, playerTurnData);

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
        Card cardToPlay = player.removeFromHand(input - 1);
        return new ClientCardPlayData(player, cardToPlay);
    }

    /**
     * Adds the drawn card to the player's hand.
     *
     * @param playerDrawnCardData The drawn card data object sent from the server that contains the
     *     drawn card.
     * @return null since this player action does not expect player to do any action
     */
    private AbstractClientData addToPlayerHand(ServerPlayerDrawnCardData playerDrawnCardData) {
        player.addToHand(playerDrawnCardData.getCard());
        return null;
    }

    /**
     * Adds the drawn card to the player's hand.
     *
     * @param playerReceivedParadeCardsData The drawn parade cards data object sent from the server
     *     that contains the drawn card(s) from the parade upon placing down the card.
     * @return null since this player action does not expect player to do any action
     */
    private AbstractClientData addToPlayerBoard(
            ServerPlayerReceivedParadeCardsData playerReceivedParadeCardsData) {
        player.addToBoard(playerReceivedParadeCardsData.getParadeCards());
        return null;
    }

    /**
     * Gets the underlying player that the LocalHumanController is controlling.
     *
     * @return The underlying Player object.
     */
    @Override
    public Player getPlayer() {
        return player;
    }
}
