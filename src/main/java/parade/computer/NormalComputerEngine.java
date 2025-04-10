package parade.computer;

import parade.card.Card;
import parade.player.Player;

import java.util.List;

/**
 * The NormalComputer class represents an AI player with a basic strategy. It attempts to minimise
 * its losses by avoiding taking too many cards.
 *
 * <p>NormalComputerEngine selects the best card to play based on a heuristic: - Prefers cards that
 * minimise the number of cards taken from the parade. - Avoids playing cards that match the colours
 * in the parade to reduce losses.
 */
public class NormalComputerEngine implements IComputerEngine {
    @Override
    public Card process(Player player, List<Player> players, List<Card> parade, int deckSize) {
        Card bestCard = null;
        int minLoss = Integer.MAX_VALUE;
        int minColourImpact = Integer.MAX_VALUE;
        List<Card> playerHand = player.getHand();

        for (Card card : playerHand) {
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

    @Override
    public String getName() {
        return "Normal";
    }
}
