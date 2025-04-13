package parade.computer;

import parade.card.*;
import parade.player.Player;
import parade.player.controller.PlayCardData;

import java.util.*;

/**
 * The NormalComputer class represents an AI player with a basic strategy. It attempts to minimise
 * its losses by avoiding taking too many cards.
 *
 * <p>NormalComputerEngine selects the best card to play based on a heuristic: - Prefers cards that
 * minimise the number of cards taken from the parade. - Avoids playing cards that match the colours
 * in the parade to reduce losses.
 */
public class NormalComputerEngine implements ComputerEngine {
    @Override
    public Card process(Player player, PlayCardData playCardData) {
        Card bestCard = null;
        int minLoss = Integer.MAX_VALUE; // Tracks fewest cards taken from parade
        int minColourImpact = Integer.MAX_VALUE; // Tracks how much the card colour matches parade

        for (Card card : player.getHand()) {
            int loss = simulateLoss(card, playCardData.getParade()); // Estimate how many cards will be taken
            int colourImpact =
                    countColourMatches(card, playCardData.getParade()); // Count parade cards with same colour

            // Prioritize moves that result in fewer cards collected.
            // If tied, prefer cards with less colour impact to avoid building towards majorities.
            if (loss < minLoss || (loss == minLoss && colourImpact < minColourImpact)) {
                minLoss = loss;
                minColourImpact = colourImpact;
                bestCard = card;
            }
        }

        return bestCard;
    }

    /**
     * Discards a card based on a simple scoring heuristic: - Prefers to discard cards with higher
     * point value. - Prefers colours that already appear frequently on the board, reducing risk of
     * majority.
     *
     * <p>The card is removed from the hand once chosen.
     *
     * @param parade The current parade lineup (not used for discard decision here).
     * @return The chosen card to be discarded.
     */
    @Override
    public Card discardCard(Player player, PlayCardData playCardData) {
        Map<Colour, Integer> boardColourCount =
                countColourOnBoard(player); // Count colours already on the board

        return player.getHand().stream()
                .max(
                        (a, b) -> {
                            // Each card's "risk score" is its value + 3×frequency of its colour on
                            // board
                            int scoreA =
                                    a.getNumber()
                                            + boardColourCount.getOrDefault(a.getColour(), 0) * 3;
                            int scoreB =
                                    b.getNumber()
                                            + boardColourCount.getOrDefault(b.getColour(), 0) * 3;
                            return Integer.compare(
                                    scoreA, scoreB); // Prefer to discard higher-risk cards
                        })
                .get();
    }

    /**
     * Counts the number of cards of each colour currently on the AI's board. Used to evaluate which
     * colours may be forming a majority.
     *
     * @return A map of Colour to frequency on the board.
     */
    private Map<Colour, Integer> countColourOnBoard(Player player) {
        Map<Colour, Integer> map = new HashMap<>();
        for (Card card : player.getBoard()) {
            map.put(card.getColour(), map.getOrDefault(card.getColour(), 0) + 1);
        }
        return map;
    }

    /**
     * Simulates how many cards would be taken from the parade if the given card is played.
     *
     * <p>A card will cause the AI to take cards from the parade if: - The card's number is greater
     * than the number of cards at the end of the parade. - Any card in the "removal zone" is of the
     * same colour or a smaller/equal number.
     *
     * @param card The card to evaluate.
     * @param parade The current parade lineup.
     * @return Estimated number of cards that would be taken.
     */
    private int simulateLoss(Card card, Parade parade) {
        int loss = 0;
        List<Card> cards = parade.getCards();
        int position = cards.size() - card.getNumber(); // Safe zone size

        for (int i = Math.max(0, position); i < cards.size(); i++) {
            Card paradeCard = cards.get(i);
            if (paradeCard.getNumber() <= card.getNumber()
                    || paradeCard.getColour().equals(card.getColour())) {
                loss++; // Would be taken by this card
            }
        }
        return loss;
    }

    /**
     * Counts how many cards in the parade match the same colour as the given card.
     *
     * <p>This is used to measure the "colour risk" of playing that card.
     *
     * @param card The card to evaluate.
     * @param parade The current parade lineup.
     * @return Number of matching colour cards in the parade.
     */
    private int countColourMatches(Card card, Parade parade) {
        int matches = 0;
        for (Card paradeCard : parade.getCards()) {
            if (paradeCard.getColour().equals(card.getColour())) {
                matches++;
            }
        }
        return matches;
    }

    @Override
    public String getName() {
        return "Normal Computer";
    }
}
