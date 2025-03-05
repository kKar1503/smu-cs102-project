package parade.player.computer;

import java.util.List;
import java.util.LinkedList;

/**
 * The HardComputer class represents an AI player with an advanced strategy.
 * This AI minimizes its own losses while maximizing the difficulty of the opponent’s next move.
 * It uses predictive analysis to determine the best card to play.
 */
public class HardComputer extends Computer {

    /**
     * Constructs a HardComputer instance with an initial hand of cards.
     *
     * @param cards The initial set of cards assigned to the computer's hand.
     */
    public HardComputer(List<Card> cards) {
        super(cards);
    }

    /**
     * Selects the best card to play by balancing two factors:
     * - Minimizing its own loss (i.e., taking as few parade cards as possible).
     * - Maximizing the difficulty for the opponent (i.e., forcing them into bad moves).
     *
     * The AI simulates the loss it would incur for each possible move and 
     * also predicts how much it can force the opponent to lose.
     *
     * @param parade The current lineup of cards in the parade.
     * @return The best card determined based on predictive analysis.
     */
    @Override
    public Card playCard(List<Card> parade) {
        Card bestCard = null;
        int minLoss = Integer.MAX_VALUE;  // Tracks the smallest number of cards taken by this AI
        int maxOpponentLoss = Integer.MIN_VALUE;  // Tracks the largest number of cards an opponent would take

        // Iterate through each card in hand to determine the best move
        for (Card card : hand) {
            int selfLoss = simulateLoss(card, parade); // How many cards this AI would take
            int opponentLoss = simulateOpponentLoss(card, parade); // How many cards the opponent might take

            /**
             * Decision-making process:
             * - Select the card that minimizes self-loss.
             * - If multiple cards result in the same loss, choose the one that maximizes opponent's loss.
             */
            if (selfLoss < minLoss || (selfLoss == minLoss && opponentLoss > maxOpponentLoss)) {
                minLoss = selfLoss;
                maxOpponentLoss = opponentLoss;
                bestCard = card;
            }
        }

        return bestCard;
    }

    /**
     * Simulates how many cards the AI would take if it plays the given card.
     * - The card's number determines how many cards remain safe in the parade.
     * - Any card in the parade with a smaller number or a matching color will be taken.
     *
     * @param card   The card being played.
     * @param parade The current parade lineup.
     * @return The number of cards this AI would take from the parade.
     */
    private int simulateLoss(Card card, List<Card> parade) {
        int loss = 0;
        int position = parade.size() - card.getNumber(); // The point in the parade where checking begins

        // Iterate through the parade from the calculated position
        for (int i = Math.max(0, position); i < parade.size(); i++) {
            Card paradeCard = parade.get(i);

            // AI will take this card if:
            // - Its number is less than or equal to the played card's number.
            // - Its color matches the played card's color.
            if (paradeCard.getNumber() <= card.getNumber() || paradeCard.getColor().equals(card.getColor())) {
                loss++;
            }
        }

        return loss; // Total number of parade cards that would be taken
    }

    /**
     * Simulates how many cards the opponent might take if the AI plays the given card.
     * - This method assumes that the opponent will try to minimize their own loss.
     * - The AI attempts to force the opponent into an unfavorable position.
     *
     * @param card   The card the AI is considering playing.
     * @param parade The current parade lineup.
     * @return The maximum number of cards an opponent might take based on this move.
     */
    private int simulateOpponentLoss(Card card, List<Card> parade) {
        // Create a simulated parade where this card has been played
        List<Card> simulatedParade = new LinkedList<>(parade);
        simulatedParade.add(card); // Add the card to the simulated parade

        int maxOpponentGain = Integer.MIN_VALUE; // Keeps track of the worst-case scenario for the opponent

        // Simulate the opponent playing each of their cards
        for (Card opponentCard : hand) {
            int opponentGain = simulateLoss(opponentCard, simulatedParade); // Compute the opponent's loss

            // Store the highest possible loss the opponent might suffer
            if (opponentGain > maxOpponentGain) {
                maxOpponentGain = opponentGain;
            }
        }

        return maxOpponentGain; // Return the worst loss the AI can force on the opponent
    }
}
