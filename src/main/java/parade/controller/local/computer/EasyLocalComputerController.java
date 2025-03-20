package parade.controller.local.computer;

import parade.common.Card;
import parade.common.state.client.AbstractClientData;
import parade.common.state.client.ClientCardPlayData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerPlayerTurnData;

import java.util.List;
import java.util.Random;

/** The EasyComputer class represents a basic AI player that plays randomly. */
public class EasyLocalComputerController extends AbstractLocalComputerController {
    /**
     * Constructs an EasyComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     */
    public EasyLocalComputerController(List<Card> cards, String name) {
        super(cards, name + "[Easy Comp]");
    }

    @Override
    public AbstractClientData send(AbstractServerData serverData)
            throws UnsupportedOperationException {
        return switch (serverData) {
            case ServerPlayerTurnData playerTurnData -> playCard(playerTurnData);
            default -> super.send(serverData);
        };
    }

    /**
     * Selects a card to play randomly from the AI player's hand.
     *
     * @param playerTurnData data object that contains sufficient information for the player to make
     *     a decision for their turn.
     * @return {@link ClientCardPlayData} object containing the card to be played.
     */
    private ClientCardPlayData playCard(ServerPlayerTurnData playerTurnData) {
        // Randomly picks any card from the hand.
        int randIdx = new Random().nextInt(getPlayer().getHand().size());
        return new ClientCardPlayData(getPlayer(), getPlayer().removeFromHand(randIdx));
    }
}
