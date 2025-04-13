package parade.computer;

import parade.card.Card;
import parade.player.Player;
import parade.player.controller.PlayCardData;

import java.util.Random;

/**
 * The EasyComputer class represents a basic AI player that plays randomly.
 *
 * <p>EasyComputerEngine selects a card to play randomly from the player's hand.
 */
public class EasyComputerEngine implements ComputerEngine {
    @Override
    public Card process(Player player, PlayCardData playCardData) {
        int randIdx = new Random().nextInt(player.getHand().size());
        return player.getHand().get(randIdx);
    }

    @Override
    public Card discardCard(Player player, PlayCardData playCardData) {
        int randIdx = new Random().nextInt(player.getHand().size());
        return player.getHand().get(randIdx);
    }

    @Override
    public String getName() {
        return "Easy Computer";
    }
}
