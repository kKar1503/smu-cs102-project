package parade.player.computer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import parade.common.Card;
import parade.common.Colour;

/**
 * The HardComputer class uses a smarter algorithm that picks cards
 * to minimize the number of cards it might need to pick up.
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
     * Selects a card to play randomly from the computer player's hand.
     *
     * @param parade The current parade lineup of cards.
     * @return A randomly chosen card to be played.
     */
    @Override
    public Card playCard(List<Card> parade) {
        return bestCard(hand, parade);
    }

    @Override
    public Card discardCard(List<Card> parade) {
        return getSmallestCard(hand);
    }

    // takes in all the cards on hand and all the cards on logic 
    // hand and parade computer
    private Card bestCard(List<Card> hand, List<Card> parade) {
        // intialise null so we can assign the final card value
        // this code is returning the best card that the computer can play
        Card best = null;
        // highest int --> smallest value possible with iterations
        // parade length
        int min = Integer.MAX_VALUE;
        int paradeLength = parade.size();

        // iterating all through cards in hand
        // for each card if this card value > parade length
        // subsequently if card value > parade length and < previous min

        for (Card card : hand) {
            // see if any number of card that allows us to skip entire parade
            int cardValue = card.getNumber();
            if (cardValue > paradeLength && cardValue < min) {
                min = cardValue;
                best = card;
            }
        }
        if (min == Integer.MAX_VALUE) {
            Colour bestColour = getMajorityColour();
            for (Card card : hand) {
                if (card.getColour() == bestColour) {
                    best = card;
                    break;
                }
            }
        }
        return best;
    }

    private Colour getMajorityColour() {
        List<Card> boardCards = this.getBoard();
        Map<Colour, Integer> result = new HashMap<>();
        for (Card card : boardCards) {
            int counter = 0;
            for (Card nestedCard : boardCards) {
                if (nestedCard.getColour() == card.getColour()) {
                    counter += 1;
                }
            }
            if (!result.containsKey(card.getColour())) {
                result.put(card.getColour(), counter);
            }
        }
        Set<Colour> resultKeys = result.keySet();
        int max = Integer.MIN_VALUE;
        Colour best = null;
        for (Colour key : resultKeys) {
            if (result.get(key) > max) {
                max = result.get(key);
                best = key;
            }
        }
        return best;
    };

    private Card getSmallestCard(List<Card> hand) {
        return hand.stream()
                .min(Comparator.comparingInt(Card::getNumber))
                .orElse(null);
    }
}
