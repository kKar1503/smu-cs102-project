package parade.controller.local.computer;

import parade.common.Card;
import parade.common.state.client.AbstractClientData;
import parade.common.state.client.ClientCardPlayData;
import parade.common.state.server.AbstractServerData;
import parade.common.state.server.ServerPlayerTurnData;

import java.util.Arrays;

/**
 * The HardComputer class represents an AI player with an advanced strategy. This AI minimises its
 * own losses while maximising the difficulty for the opponent. It uses predictive analysis to
 * determine the best card to play.
 */
public class HardLocalComputerController extends AbstractLocalComputerController {

    /** Constructs a HardComputer instance with an initial hand of cards. */
    public HardLocalComputerController(String name) {
        super(name + "[Hard Comp]");
    }

    @Override
    public AbstractClientData send(AbstractServerData serverData)
            throws UnsupportedOperationException {
        return switch (serverData) {
            case ServerPlayerTurnData playerTurnData -> playCard(playerTurnData);
            default -> super.send(serverData);
        };
    }

    /**
     * Selects the best card to play by balancing two factors: - Minimising its own loss (i.e.,
     * taking as few parade cards as possible). - Maximising the difficulty for the opponent (i.e.,
     * forcing them into bad moves).
     *
     * <p>The AI simulates the loss it would incur for each possible move and also predicts how much
     * it can force the opponent to lose.
     *
     * @param playerTurnData data object that contains sufficient information for the player to make
     *     a decision for their turn.
     * @return {@link ClientCardPlayData} object containing the card to be played.
     */
    public ClientCardPlayData playCard(ServerPlayerTurnData playerTurnData) {
        Card bestCard = null;
        int minLoss = Integer.MAX_VALUE; // Tracks the smallest number of cards taken by this AI
        int maxOpponentLoss =
                Integer.MIN_VALUE; // Tracks the largest number of cards an opponent would take

        // Iterate through each card in hand to determine the best move
        for (Card card : getPlayer().getHand()) {
            int selfLoss =
                    simulateLoss(
                            card, playerTurnData.getParade()); // How many cards this AI would take
            int opponentLoss =
                    simulateOpponentLoss(
                            card,
                            playerTurnData.getParade()); // How many cards the opponent might take

            // Decision-making process: - Select the card that minimises self-loss. - If multiple
            // cards result in the same loss, choose the one that maximises opponent's loss.
            if (selfLoss < minLoss || (selfLoss == minLoss && opponentLoss > maxOpponentLoss)) {
                minLoss = selfLoss;
                maxOpponentLoss = opponentLoss;
                bestCard = card;
            }
        }

        return new ClientCardPlayData(getPlayer(), bestCard);
    }

    /**
     * Simulates how many cards the AI would take if it plays the given card. - The card's number
     * determines how many cards remain safe in the parade. - Any card in the parade with a smaller
     * number or a matching colour will be taken.
     *
     * @param card The card being played.
     * @param parade The current parade lineup.
     * @return The number of cards this AI would take from the parade.
     */
    private int simulateLoss(Card card, Card[] parade) {
        int loss = 0;
        int position =
                parade.length - card.getNumber(); // The point in the parade where checking begins

        // Iterate through the parade from the calculated position
        for (int i = Math.max(0, position); i < parade.length; i++) {
            Card paradeCard = parade[i];

            // AI will take this card if:
            // - Its number is less than or equal to the played card's number.
            // - Its colour matches the played card's colour.
            if (paradeCard.getNumber() <= card.getNumber()
                    || paradeCard.getColour().equals(card.getColour())) {
                loss++;
            }
        }

        return loss; // Total number of parade cards that would be taken
    }

    /**
     * Simulates how many cards the opponent might take if the AI plays the given card. - This
     * method assumes that the opponent will try to minimise their own loss. - The AI attempts to
     * force the opponent into an unfavourable position.
     *
     * @param card The card the AI is considering playing.
     * @param parade The current parade lineup.
     * @return The maximum number of cards an opponent might take based on this move.
     */
    private int simulateOpponentLoss(Card card, Card[] parade) {
        // Create a simulated parade where this card has been played
        Card[] simulatedParade = Arrays.copyOf(parade, parade.length + 1);
        simulatedParade[simulatedParade.length - 1] = card; // Add the card to the simulated parade

        int maxOpponentGain =
                Integer.MIN_VALUE; // Keeps track of the worst-case scenario for the opponent

        // Simulate the opponent playing each of their cards
        for (Card opponentCard : getPlayer().getHand()) {
            int opponentGain =
                    simulateLoss(opponentCard, simulatedParade); // Compute the opponent's loss

            // Store the highest possible loss the opponent might suffer
            if (opponentGain > maxOpponentGain) {
                maxOpponentGain = opponentGain;
            }
        }

        return maxOpponentGain; // Return the worst loss the AI can force on the opponent
    }
}
