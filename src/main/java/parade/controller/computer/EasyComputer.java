package parade.controller.computer;

import java.util.List;
import java.util.Random;

import parade.common.Card;

/** The EasyComputer class represents a basic AI player that plays randomly. */
public class EasyComputer extends AbstractComputer {

    /**
     * Constructs an EasyComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     */
    public EasyComputer(List<Card> cards) {
        super(cards, "Easy AI");
    }

    /**
     * Selects a card to play randomly from the AI player's hand.
     *
     * @param parade The current parade lineup of cards.
     * @return A randomly chosen card to be played.
     */
    @Override
    public Card playCard(List<Card> parade) {
        Random rand = new Random();
        return hand.get(rand.nextInt(hand.size())); // Randomly picks any card from the hand.
    }
}
