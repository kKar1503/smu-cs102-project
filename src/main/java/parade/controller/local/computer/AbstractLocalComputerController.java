package parade.controller.local.computer;

import parade.common.Card;
import parade.common.Player;
import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerGameStartData;
import parade.common.state.server.ServerPlayerDrawnCardData;
import parade.common.state.server.ServerPlayerReceivedParadeCardsData;
import parade.controller.local.ILocalPlayerController;

/**
 * The Computer class is an abstract representation of an AI player in the game. It contains common
 * functionality shared among different AI difficulty levels.
 */
public abstract class AbstractLocalComputerController implements ILocalPlayerController {
    private final Player player;

    /**
     * Constructs a Computer player with a given name and initial hand.
     *
     * @param name The name of the AI player.
     */
    public AbstractLocalComputerController(String name) {
        player = new Player(name);
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
            case ServerGameStartData gameStartData -> addToPlayerHand(gameStartData.getCards());
            case ServerPlayerDrawnCardData playerDrawnCardData ->
                    addToPlayerHand(playerDrawnCardData.getCard());
            case ServerPlayerReceivedParadeCardsData playerReceivedParadeCardsData ->
                    addToPlayerBoard(playerReceivedParadeCardsData.getParadeCards());
            default -> throw new UnsupportedOperationException("Unexpected value: " + serverData);
        };
    }

    /**
     * Adds the drawn card to the player's hand.
     *
     * @param cards The array of cards sent from the server that are drawn to the hand.
     * @return null since this player action does not expect player to do any action
     */
    protected AbstractClientData addToPlayerHand(Card... cards) {
        player.addToBoard(cards);
        return null;
    }

    /**
     * Adds the drawn card to the player's board.
     *
     * @param cards The array of parade cards sent from the server that are drawn from the parade
     *     upon placing down the card.
     * @return null since this player action does not expect player to do any action
     */
    private AbstractClientData addToPlayerBoard(Card... cards) {
        player.addToBoard(cards);
        return null;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
