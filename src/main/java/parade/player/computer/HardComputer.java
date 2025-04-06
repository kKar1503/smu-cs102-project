package parade.player.computer;

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

    // Chooses the best card based on the parade state and hand
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

        // if (best == null) {
        //     System.out.println("FUCKKKING LOGIC 2");
        //     Colour bestColour = getMajorityColour();
        //     for (Card card : hand) {
        //         if (card.getColour() == bestColour) {
        //             best = card;
        //             break;
        //         }
        //     }
        // }

        // if (best != null) {
        //     return best;
        // }

        Random rand = new Random();
        return hand.get(rand.nextInt(hand.size()));
    }

    // // Finds the colour that appears the most on the board
    // private Colour getMajorityColour() {
    //     List<Card> boardCards = this.getBoard();
    //     Map<Colour, Integer> result = new HashMap<>();

    //     for (Card card : boardCards) {
    //         int counter = 0;
    //         for (Card nestedCard : boardCards) {
    //             if (nestedCard.getColour() == card.getColour()) {
    //                 counter += 1;
    //             }
    //         }

    //         if (!result.containsKey(card.getColour())) {
    //             result.put(card.getColour(), counter);
    //         }
    //     }

    //     Set<Colour> resultKeys = result.keySet();
    //     int max = Integer.MIN_VALUE;
    //     Colour best = null;

    //     for (Colour key : resultKeys) {
    //         if (result.get(key) > max) {
    //             max = result.get(key);
    //             best = key;
    //         }
    //     }

    //     return best;
    // }

    // Returns the card with the highest number from hand
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
