package parade.player.computer;

import parade.common.Card;

import java.util.*;

/**
 * The HardComputer class uses a smarter algorithm that picks cards to minimize the number of cards
 * it might need to pick up.
 */
public class HardComputer extends Computer {

    /**
     * Constructs a HardComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the computer player's hand.
     */
    public HardComputer(List<Card> cards, String name) {
        super(cards, name + "[Hard Comp]");
    }

    /**
     * Selects a card to play based on minimizing risk.
     *
     * @param parade The current parade lineup of cards.
     * @return The chosen card to be played.
     */
    @Override
    public Card playCard(List<Card> parade) {
        return bestCard(hand, parade);
    }

    /**
     * Chooses the highest-numbered card to discard.
     *
     * @param parade The current parade lineup of cards.
     * @return The card to discard.
     */
    @Override
    public Card discardCard(List<Card> parade) {
        return getHighestCard(hand);
    }

    private Card bestCard(List<Card> hand, List<Card> parade) {
        Card best = null;
        int min = Integer.MAX_VALUE;
        int paradeLength = parade.size();

        for (Card card : hand) {
            int cardValue = card.getNumber();
            if (cardValue > paradeLength && cardValue < min) {
                min = cardValue;
                best = card;
            }
        }

        Random rand = new Random();
        return hand.get(rand.nextInt(hand.size()));
    }

    private Card getHighestCard(List<Card> hand) {
        int highest = Integer.MIN_VALUE;
        Card highestCard = null;

        for (Card card : hand) {
            if (card.getNumber() > highest) {
                highest = card.getNumber();
                highestCard = card;
            }
        }

        return highestCard;
    }
}
