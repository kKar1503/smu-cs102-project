package parade.player.computer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parade.common.Card;
import parade.common.Colour;

/**
 * The NormalComputer class represents an AI player with a basic strategy.
 * It attempts to minimize its losses by:
 * - Avoiding taking too many cards from the parade.
 * - Reducing the risk of collecting too many cards of the same colour.
 * - Discarding high-value or redundant cards that may negatively affect score or majority.
 */
public class NormalComputer extends Computer {

    /**
     * Constructs a NormalComputer instance with an initial hand of cards and a name.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     * @param name The base name of the AI player (difficulty label will be appended).
     */
    public NormalComputer(List<Card> cards, String name) {
        super(cards, name + "[Normal Comp]");
    }

    /**
     * Selects the best card to play based on two heuristics:
     * - Minimizing the number of cards that will be taken from the parade.
     * - Avoiding cards that match colours already present in the parade (to reduce risk).
     *
     * The card is removed from the AI's hand once selected.
     *
     * @param parade The current lineup of cards in the parade.
     * @return The chosen card to be played.
     */
    @Override
    public Card playCard(List<Card> parade) {
        Card bestCard = null;
        int minLoss = Integer.MAX_VALUE;             // Tracks fewest cards taken from parade
        int minColourImpact = Integer.MAX_VALUE;     // Tracks how much the card colour matches parade

        for (Card card : hand) {
            int loss = simulateLoss(card, parade);               // Estimate how many cards will be taken
            int colourImpact = countColourMatches(card, parade); // Count parade cards with same colour

            // Prioritize moves that result in fewer cards collected.
            // If tied, prefer cards with less colour impact to avoid building towards majorities.
            if (loss < minLoss || (loss == minLoss && colourImpact < minColourImpact)) {
                minLoss = loss;
                minColourImpact = colourImpact;
                bestCard = card;
            }
        }

        hand.remove(bestCard); // Remove the selected card from hand
        return bestCard;
    }

    /**
     * Discards a card based on a simple scoring heuristic:
     * - Prefers to discard cards with higher point value.
     * - Prefers colours that already appear frequently on the board, reducing risk of majority.
     *
     * The card is removed from the hand once chosen.
     *
     * @param parade The current parade lineup (not used for discard decision here).
     * @return The chosen card to be discarded.
     */
    @Override
    public Card discardCard(List<Card> parade) {
        Map<Colour, Integer> boardColourCount = countColourOnBoard(); // Count colours already on the board

        return hand.stream()
            .max((a, b) -> {
                // Each card's "risk score" is its value + 3×frequency of its colour on board
                int scoreA = a.getNumber() + boardColourCount.getOrDefault(a.getColour(), 0) * 3;
                int scoreB = b.getNumber() + boardColourCount.getOrDefault(b.getColour(), 0) * 3;
                return Integer.compare(scoreA, scoreB); // Prefer to discard higher-risk cards
            })
            .map(card -> {
                hand.remove(card); // Remove and return the selected card
                return card;
            })
            .orElse(hand.removeFirst()); // Fallback in case no card found (shouldn't happen)
    }

    /**
     * Counts the number of cards of each colour currently on the AI's board.
     * Used to evaluate which colours may be forming a majority.
     *
     * @return A map of Colour to frequency on the board.
     */
    private Map<Colour, Integer> countColourOnBoard() {
        Map<Colour, Integer> map = new HashMap<>();
        for (Card card : board) {
            map.put(card.getColour(), map.getOrDefault(card.getColour(), 0) + 1);
        }
        return map;
    }

    /**
     * Simulates how many cards would be taken from the parade if the given card is played.
     *
     * A card will cause the AI to take cards from the parade if:
     * - The card's number is greater than the number of cards at the end of the parade.
     * - Any card in the "removal zone" is of the same colour or a smaller/equal number.
     *
     * @param card The card to evaluate.
     * @param parade The current parade lineup.
     * @return Estimated number of cards that would be taken.
     */
    private int simulateLoss(Card card, List<Card> parade) {
        int loss = 0;
        int position = parade.size() - card.getNumber(); // Safe zone size

        for (int i = Math.max(0, position); i < parade.size(); i++) {
            Card paradeCard = parade.get(i);
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
     * This is used to measure the "colour risk" of playing that card.
     *
     * @param card The card to evaluate.
     * @param parade The current parade lineup.
     * @return Number of matching colour cards in the parade.
     */
    private int countColourMatches(Card card, List<Card> parade) {
        int matches = 0;
        for (Card paradeCard : parade) {
            if (paradeCard.getColour().equals(card.getColour())) {
                matches++;
            }
        }
        return matches;
    }
}
