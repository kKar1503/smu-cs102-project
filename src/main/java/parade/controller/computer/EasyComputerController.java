package parade.controller.computer;

import parade.common.Card;
import parade.common.state.server.PlayerTurnData;

import java.util.List;
import java.util.Random;

/** The EasyComputer class represents a basic AI player that plays randomly. */
public class EasyComputerController extends AbstractComputerController {

    /**
     * Constructs an EasyComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     */
    public EasyComputerController(List<Card> cards, String name) {
        super(cards, name + "[Easy Comp]");
    }

    /**
     * Selects a card to play randomly from the AI player's hand.
     *
     * @param playerTurnData data object that contains sufficient information for the player to make
     *     a decision for their turn.
     * @return A randomly chosen card to be played.
     */
    @Override
    public Card playCard(PlayerTurnData playerTurnData) {
        Random rand = new Random();
        return player.removeFromHand(
                rand.nextInt(player.getHand().size())); // Randomly picks any card from the hand.
    }
}
