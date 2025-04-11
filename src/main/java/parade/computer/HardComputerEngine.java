package parade.computer;

import parade.card.Card;
import parade.card.Colour;
import parade.card.Parade;
import parade.player.Player;

import java.util.ArrayList;
import java.util.List;

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
        double bestDelta = Double.MAX_VALUE;

        List<Card> hand = new ArrayList<>(player.getHand());

        for (Card candidateCard : hand) {
            Parade paradeCopy1 = new Parade(parade.getCards()); // Copy of parade
            List<Card> tempBoard = new ArrayList<>(player.getBoard()); // Copy of player’s board

            List<Card> takenCards = simulateParadeRemoval(paradeCopy1, candidateCard);
            tempBoard.addAll(takenCards);

            int currentScore = calculateScore(tempBoard, candidateCard.getColour());

            List<Double> playerBestDeltas = new ArrayList<>();
            for (Player otherPlayer : players) {
                double maxOpponentScore = 0;

                for (Card opponentCard : otherPlayer.getHand()) {
                    Parade paradeCopy2 = new Parade(parade.getCards());
                    List<Card> tempOppBoard = new ArrayList<>(otherPlayer.getBoard());

                    List<Card> oppTaken = simulateParadeRemoval(paradeCopy2, opponentCard);
                    tempOppBoard.addAll(oppTaken);

                    int opponentScore = calculateScore(tempOppBoard, opponentCard.getColour());
                    maxOpponentScore = Math.max(maxOpponentScore, opponentScore);
                }

                double delta = maxOpponentScore - currentScore;
                playerBestDeltas.add(delta);
            }

            double avgDelta =
                    playerBestDeltas.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(Double.MAX_VALUE);

            if (avgDelta < bestDelta) {
                bestDelta = avgDelta;
                bestCard = candidateCard;
            } c
        }

        return bestCard;
    }

    /**
     * Simulates playing a card and collecting the appropriate cards from the parade. Modifies the
     * parade by removing collected cards.
     */
    public List<Card> simulateParadeRemoval(List<Card> cards, Card placeCard) {

        // List to store cards to be removed
        List<Card> removedCards = new ArrayList<>();

        // Remove mode
        if ((cards).size() > placeCard.getNumber()) {

            int removeZoneCardIndex = cards.size() - placeCard.getNumber();
            // Count from index of numbers
            for (int i = 0; i < removeZoneCardIndex; i++) { // i here is the index

                // Obtains card to compare
                Card cardAtIndex = cards.get(i);

                // Check which ones to remove (equal or less than)
                if (cardAtIndex.getNumber() <= placeCard.getNumber()
                        || cardAtIndex.getColour() == placeCard.getColour()) {
                    removedCards.add(cardAtIndex);
                }
            }

            // Remove from the cards
            cards.removeAll(removedCards);

            // Add placeCard
            cards.add(placeCard);
        }

        return removedCards;
    }

    /** Calculates the score of a board based on the card majority rules. */
    private int calculateScore(List<Card> board, Colour majorityColour) {
        int score = 0;
        for (Card card : board) {
            if (card.getColour() == majorityColour) {
                score += 1;
            } else {
                score += card.getNumber();
            }
        }
        return score;
    }

    @Override
    public String getName() {
        return "Hard";
    }
}
