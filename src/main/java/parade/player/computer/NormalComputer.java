package parade.player.computer;

import parade.common.Card;

import java.util.List;
import java.util.Random;

/**
 * The NormalComputer class represents an AI player with a basic strategy. It attempts to minimise
 * its losses by avoiding taking too many cards.
 */
public class NormalComputer extends Computer {

    /**
     * Constructs a NormalComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     */
    public NormalComputer(List<Card> cards, String name) {
        super(cards, name + "[Normal Comp]");
    }

    /**
     * Selects the best card to play based on a heuristic: - Prefers cards that minimise the number
     * of cards taken from the parade. - Avoids playing cards that match the colours in the parade
     * to reduce losses.
     *
     * @param parade The current parade lineup of cards.
     * @return The best card determined by the heuristic.
     */
    @Override
    public Card playCard(List<Card> parade) {
        Card bestCard = null;
        int minLoss = Integer.MAX_VALUE;
        int minColourImpact = Integer.MAX_VALUE;

        for (Card card : hand) {
            int loss = simulateLoss(card, parade);
            int colourImpact = countColourMatches(card, parade);

            if (loss < minLoss || (loss == minLoss && colourImpact < minColourImpact)) {
                minLoss = loss;
                minColourImpact = colourImpact;
                bestCard = card;
            }
        }
        return bestCard;
    }

    @Override
    public Card discardCard(List<Card> parade) {
        Random rand = new Random();
        return hand.get(rand.nextInt(hand.size())); // Randomly picks any card to discard.
    }

    /**
     * Simulates how many cards would be taken if the given card is played.
     *
     * @param card The card to be played.
     * @param parade The current parade lineup.
     * @return The number of cards the AI would take.
     */
    private int simulateLoss(Card card, List<Card> parade) {
        int loss = 0;
        int position = parade.size() - card.getNumber();

        for (int i = Math.max(0, position); i < parade.size(); i++) {
            Card paradeCard = parade.get(i);
            if (paradeCard.getNumber() <= card.getNumber()
                    || paradeCard.getColour().equals(card.getColour())) {
                loss++;
            }
        }
        return loss;
    }

    /**
     * Counts how many cards in the parade match the colour of the given card.
     *
     * @param card The card to check against the parade.
     * @param parade The current parade lineup.
     * @return The number of matching colours.
     */
    private int countColourMatches(Card card, List<Card> parade) {
        int colourMatches = 0;
        for (Card paradeCard : parade) {
            if (paradeCard.getColour().equals(card.getColour())) {
                colourMatches++;
            }
        }
        return colourMatches;
    }
}
