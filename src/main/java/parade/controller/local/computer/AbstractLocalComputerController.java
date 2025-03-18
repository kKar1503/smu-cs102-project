package parade.controller.local.computer;

import parade.common.Card;
import parade.common.Player;
import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerPlayerDrawnCardData;
import parade.common.state.server.ServerPlayerReceivedParadeCardsData;
import parade.controller.local.ILocalPlayerController;

import java.util.List;

/**
 * The Computer class is an abstract representation of an AI player in the game. It contains common
 * functionality shared among different AI difficulty levels.
 */
public abstract class AbstractLocalComputerController implements ILocalPlayerController {
    private Player player;

    /**
     * Constructs a Computer player with a given name and initial hand.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     * @param name The name of the AI player.
     */
    public AbstractLocalComputerController(List<Card> cards, String name) {
        player = new Player(name);
        player.addToHand(cards.toArray(Card[]::new));
    }

    /**
     * Processes the server data received from the game server. This method is responsible for
     * updating the player hand and board using two implementations of {@link AbstractServerData}
     * abstract class:
     *
     * <ul>
     *   <li>{@link ServerPlayerDrawnCardData} - Represents the data for a drawn card.
     *   <li>{@link ServerPlayerReceivedParadeCardsData} - Represents the data for received parade
     *       cards.
     * </ul>
     *
     * @param serverData an {@link AbstractServerData} object which contains information for the
     *     player to act.
     * @return null for both the {@link AbstractServerData} subclasses that this method handles.
     * @throws UnsupportedOperationException if the server data is not of the expected type.
     */
    public AbstractClientData send(AbstractServerData serverData)
            throws UnsupportedOperationException {
        return switch (serverData) {
            case ServerPlayerDrawnCardData playerDrawnCardData ->
                    addToPlayerHand(playerDrawnCardData);
            case ServerPlayerReceivedParadeCardsData playerReceivedParadeCardsData ->
                    addToPlayerBoard(playerReceivedParadeCardsData);
            default -> throw new UnsupportedOperationException("Unexpected value: " + serverData);
        };
    }

    /**
     * Adds the drawn card to the player's hand.
     *
     * @param playerDrawnCardData The drawn card data object sent from the server that contains the
     *     drawn card.
     * @return null since this player action does not expect player to do any action
     */
    protected AbstractClientData addToPlayerHand(ServerPlayerDrawnCardData playerDrawnCardData) {
        player.addToBoard(playerDrawnCardData.getCard());
        return null;
    }

    /**
     * Adds the drawn card to the player's board.
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

    @Override
    public Player getPlayer() {
        return player;
    }
}
