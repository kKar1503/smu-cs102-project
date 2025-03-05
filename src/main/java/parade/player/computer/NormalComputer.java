package parade.player.computer;

import parade.common.Card;

import java.util.List;

/**
 * The NormalComputer class represents an AI player that makes slightly intelligent decisions when
 * playing a card. It tries to minimize its losses by avoiding taking too many cards and avoiding
 * color matches with the parade.
 */
public class NormalComputer extends Computer {

    /**
     * Constructs a NormalComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the computer's hand.
     */
    public NormalComputer(List<Card> cards) {
        super(cards);
    }

    /**
     * Selects the best card to play based on a simple heuristic: - Prefers cards that minimize the
     * number of cards taken from the parade. - Avoids matching the color of cards in the parade (to
     * reduce loss).
     *
     * @param parade The current parade lineup of cards.
     * @return The best card determined by its heuristic.
     */
    @Override
    public Card playCard(List<Card> parade) {
        Card bestCard = null;
        int minLoss = Integer.MAX_VALUE;
        int minColorImpact = Integer.MAX_VALUE;

        // Iterate over all possible cards to find the best one
        for (Card card : hand) {
            int loss = simulateLoss(card, parade);
            int colorImpact = countColorMatches(card, parade);

            // The best card is the one with the least loss and least color impact
            if (loss < minLoss || (loss == minLoss && colorImpact < minColorImpact)) {
                minLoss = loss;
                minColorImpact = colorImpact;
                bestCard = card;
            }
        }
        return bestCard;
    }

    /**
     * Simulates how many cards would be taken if the given card is played. - A card's number
     * determines how many cards are safe from being taken. - Any card in the parade with a smaller
     * number or a matching color is taken.
     *
     * @param card The card to be played.
     * @param parade The current parade lineup.
     * @return The number of cards the player would take.
     */
    private int simulateLoss(Card card, List<Card> parade) {
        int loss = 0;
        int position = parade.size() - card.getNumber();

        // Check all affected cards in the parade
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
     * Counts how many cards in the parade match the color of the given card. - The higher the
     * number of matches, the riskier the card is.
     *
     * @param card The card to check against the parade.
     * @param parade The current parade lineup.
     * @return The number of color matches.
     */
    private int countColorMatches(Card card, List<Card> parade) {
        int colorMatches = 0;
        for (Card paradeCard : parade) {
            if (paradeCard.getColour().equals(card.getColour())) {
                colorMatches++;
            }
        }
        return colorMatches;
    }
}
