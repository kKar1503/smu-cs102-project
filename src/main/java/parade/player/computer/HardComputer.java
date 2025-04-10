package parade.player.computer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parade.common.Card;
import parade.common.Colour;

/**
 * The HardComputer class represents an advanced AI player that uses a multi-factor strategy.
 * It balances:
 * - Minimizing its own loss (cards collected from the parade),
 * - Avoiding high-risk colour matches,
 * - Maximizing the number of cards the next player might be forced to take.
 *
 * This AI simulates possible moves and assigns scores based on weighted heuristics.
 */
public class HardComputer extends Computer {

    /**
     * Constructs a HardComputer instance with an initial hand of cards and a name.
     *
     * @param cards The initial hand of the AI player.
     * @param name The base name of the AI (difficulty label will be appended).
     */
    public HardComputer(List<Card> cards, String name) {
        super(cards, name + "[Hard Comp]");
    }

    /**
     * Selects the optimal card to play by evaluating:
     * - How many cards it will collect (self-loss),
     * - How much the played card's colour matches the parade (colour penalty),
     * - How many cards it can potentially force the next player to collect (opponent impact).
     *
     * The evaluation uses a weighted scoring function to choose the best overall move.
     * The selected card is removed from the hand before returning.
     *
     * @param parade The current list of cards in the parade.
     * @return The best card to play based on heuristic scoring.
     */
    @Override
    public Card playCard(List<Card> parade) {
        Card bestCard = null;
        int bestScore = Integer.MAX_VALUE; // Lower is better

        for (Card card : hand) {
            int selfLoss = simulateLoss(card, parade);                    // Cards AI will take
            int colorPenalty = countColourMatches(card, parade);         // Colour match risk
            int simulatedImpact = simulateWorstOpponentLoss(card, parade); // How many cards next player might take

            // Heuristic weight scoring:
            // - Self loss is weighted most heavily
            // - Colour penalty moderately
            // - Opponent impact is desirable (so subtracted)
            int score = (selfLoss * 4) + (colorPenalty * 2) - (simulatedImpact * 3);

            if (score < bestScore) {
                bestScore = score;
                bestCard = card;
            }
        }

        hand.remove(bestCard); // Remove chosen card from hand
        return bestCard;
    }

    /**
     * Discards a card based on a "risk score" that combines:
     * - High value (number) of the card,
     * - Frequency of the card's colour already present on the board.
     *
     * This aims to remove cards that are likely to contribute negatively to scoring.
     *
     * @param parade The current parade lineup (not used in this heuristic).
     * @return The discarded card.
     */
    @Override
    public Card discardCard(List<Card> parade) {
        Map<Colour, Integer> boardColourCount = countColourOnBoard();

        return hand.stream()
            .max((a, b) -> {
                // Score = card number + 5×frequency of card's colour on board
                int scoreA = a.getNumber() + boardColourCount.getOrDefault(a.getColour(), 0) * 5;
                int scoreB = b.getNumber() + boardColourCount.getOrDefault(b.getColour(), 0) * 5;
                return Integer.compare(scoreA, scoreB);
            })
            .map(card -> {
                hand.remove(card);
                return card;
            })
            .orElse(hand.removeFirst()); // Fallback if no card chosen
    }

    /**
     * Calculates how many cards of each colour are currently on the AI's board.
     * Used for determining potential majority colour threats.
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
     * Simulates the number of parade cards the AI would be forced to take
     * if it played the given card. This is based on Parade rules:
     * - Cards are taken if they match in colour,
     * - Or if their number is less than or equal to the played card's number.
     *
     * @param card The card being considered for play.
     * @param parade The current state of the parade.
     * @return The estimated number of cards that would be collected.
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
     * Counts how many cards in the parade have the same colour as the given card.
     * This helps evaluate risk of collecting more of that colour (toward majority).
     *
     * @param card The card being evaluated.
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

    /**
     * Simulates the worst-case scenario for the next player after this AI plays a card.
     * It assumes the next player could play any of this AI's current cards.
     *
     * This method estimates how many cards the next player might be forced to take,
     * to influence card choice for offensive value.
     *
     * @param playedCard The card the AI is considering playing.
     * @param parade The current parade lineup.
     * @return The worst-case number of cards the next player may collect.
     */
    private int simulateWorstOpponentLoss(Card playedCard, List<Card> parade) {
        List<Card> simulatedParade = new ArrayList<>(parade);
        simulatedParade.add(playedCard); // Simulate the played card being added

        int maxLoss = 0;
        for (Card opponentCard : hand) { // Simulate the next player using each card in our hand
            int loss = simulateLoss(opponentCard, simulatedParade);
            if (loss > maxLoss) {
                maxLoss = loss;
            }
        }
        return maxLoss;
    }
}
