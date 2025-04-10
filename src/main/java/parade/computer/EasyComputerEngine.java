package parade.computer;

import parade.card.Card;
import parade.player.Player;

import java.util.List;
import java.util.Random;

/**
 * The EasyComputer class represents a basic AI player that plays randomly.
 *
 * <p>EasyComputerEngine selects a card to play randomly from the player's hand.
 */
public class EasyComputerEngine implements IComputerEngine {
    @Override
    public Card process(Player player, List<Player> players, List<Card> parade, int deckSize) {
        // Randomly picks any card from the hand.
        int randIdx = new Random().nextInt(player.getHand().size());
        return player.getHand().get(randIdx);
    }

    @Override
    public String getName() {
        return "Easy";
    }
}
