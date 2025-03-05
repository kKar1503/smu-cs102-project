package parade.player.computer;

import java.util.List;
import java.util.Random;

/**
 * The EasyComputer class represents a simple AI player
 * that plays completely randomly with no strategic decision-making.
 * It extends the abstract Computer class and implements the playCard method.
 */
public class EasyComputer extends Computer {

    /**
     * Constructs an EasyComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the computer's hand.
     */
    public EasyComputer(List<Card> cards) {
        super(cards);
    }

    /**
     * Selects a card to play from the hand completely at random.
     *
     * @param parade The current parade lineup of cards.
     * @return The randomly chosen card to be played.
     */
    @Override
    public Card playCard(List<Card> parade) {
        Random rand = new Random();
        return hand.get(rand.nextInt(hand.size())); // Picks any random card from the hand.
    }
}
