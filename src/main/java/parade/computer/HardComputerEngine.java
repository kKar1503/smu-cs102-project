package parade.computer;

import parade.card.Card;
import parade.card.Colour;
import parade.card.Parade;
import parade.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The HardComputer class represents an AI player with an advanced strategy. This AI minimises its
 * own losses while maximising the difficulty for the opponent. It uses predictive analysis to
 * determine the best card to play.
 *
 * <p>HardComputerEngine selects the best card to play by balancing two factors: - Minimising its
 * own loss (i.e., taking as few parade cards as possible). - Maximising the difficulty for the
 * opponent (i.e., forcing them into bad moves).
 *
 * <p>This engine simulates the loss it would incur for each possible move and also predicts how
 * much it can force the opponent to lose.
 */
public class HardComputerEngine implements IComputerEngine {
    @Override
    public Card process(Player player, List<Player> players, Parade parade, int deckSize) {
        Card bestCard = null;
        int bestScore = Integer.MAX_VALUE; // Lower is better

        for (Card card : player.getHand()) {
            int selfLoss = simulateLoss(card, parade.getCards()); // Cards AI will take
            int colorPenalty = countColourMatches(card, parade.getCards()); // Colour match risk
            int simulatedImpact =
                    simulateWorstOpponentLoss(
                            player,
                            card,
                            parade.getCards()); // How many cards next player might take

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

        return bestCard;
    }

    /**
     * Discards a card based on a "risk score" that combines: - High value (number) of the card, -
     * Frequency of the card's colour already present on the board.
     *
     * <p>This aims to remove cards that are likely to contribute negatively to scoring.
     *
     * @param parade The current parade lineup (not used in this heuristic).
     * @return The discarded card.
     */
    @Override
    public Card discardCard(Player player, Parade parade) {
        Map<Colour, Integer> boardColourCount = countColourOnBoard(player);

        return player.getHand().stream()
                .max(
                        (a, b) -> {
                            // Score = card number + 5×frequency of card's colour on board
                            int scoreA =
                                    a.getNumber()
                                            + boardColourCount.getOrDefault(a.getColour(), 0) * 5;
                            int scoreB =
                                    b.getNumber()
                                            + boardColourCount.getOrDefault(b.getColour(), 0) * 5;
                            return Integer.compare(scoreA, scoreB);
                        })
                .get();
    }

    /**
     * Calculates how many cards of each colour are currently on the AI's board. Used for
     * determining potential majority colour threats.
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
     * Simulates the number of parade cards the AI would be forced to take if it played the given
     * card. This is based on Parade rules: - Cards are taken if they match in colour, - Or if their
     * number is less than or equal to the played card's number.
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
     * Counts how many cards in the parade have the same colour as the given card. This helps
     * evaluate risk of collecting more of that colour (toward majority).
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
     * Simulates the worst-case scenario for the next player after this AI plays a card. It assumes
     * the next player could play any of this AI's current cards.
     *
     * <p>This method estimates how many cards the next player might be forced to take, to influence
     * card choice for offensive value.
     *
     * @param playedCard The card the AI is considering playing.
     * @param parade The current parade lineup.
     * @return The worst-case number of cards the next player may collect.
     */
    private int simulateWorstOpponentLoss(Player player, Card playedCard, List<Card> parade) {
        List<Card> simulatedParade = new ArrayList<>(parade);
        simulatedParade.add(playedCard); // Simulate the played card being added

        int maxLoss = 0;
        for (Card opponentCard :
                player.getHand()) { // Simulate the next player using each card in our hand
            int loss = simulateLoss(opponentCard, simulatedParade);
            if (loss > maxLoss) {
                maxLoss = loss;
            }
        }
        return maxLoss;
    }

    @Override
    public String getName() {
        return "Hard";
    }
}
