package parade.computer;

import parade.common.Card;
import parade.common.Player;

import java.util.Random;

/**
 * The EasyComputer class represents a basic AI player that plays randomly.
 *
 * <p>EasyComputerEngine selects a card to play randomly from the player's hand.
 */
public class EasyComputerEngine implements IComputerEngine {
    @Override
    public Card process(Player player, Player[] players, Card[] parade, int deckSize) {
        // Randomly picks any card from the hand.
        int randIdx = new Random().nextInt(player.getHand().size());
        return player.getHand().get(randIdx);
    }

    @Override
    public String getName() {
        return "Easy";
    }
}
