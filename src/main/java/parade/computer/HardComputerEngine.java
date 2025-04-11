package parade.computer;

import parade.card.*;
import parade.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
public class HardComputerEngine implements ComputerEngine {

    public Card process(Player player, List<Player> players, Parade parade, int deckSize) {
        Card bestCard = player.getHand().get(0);
        double bestDelta = Double.MAX_VALUE;

        List<Card> hand = new ArrayList<>(player.getHand());

        for (Card candidateCard : hand) {
            Parade paradeCopy1 = new Parade(parade); // Copy of parade
            List<Card> tempBoard = new ArrayList<>(player.getBoard()); // Copy of player’s board

            List<Card> takenCards = simulateParadeRemoval(paradeCopy1.getCards(), candidateCard); // List of removed cards
            tempBoard.addAll(takenCards); // added removed cards to temp board

            // why just one colour, list of majority colours
            List<Colour> majorityColours = decideMajority(player, players, getPlayerBoardMaps(players));
            int currentScore = calculateScore(tempBoard, majorityColours);

            List<Double> playerBestDeltas = new ArrayList<>();
            for (Player otherPlayer : players) {
                double bestOpponentScore = 0;
                if (!player.equals(otherPlayer)) {
                    continue;
                }
                for (Card opponentCard : otherPlayer.getHand()) {
                    Parade paradeCopy2 = new Parade(parade);
                    List<Card> tempOppBoard = new ArrayList<>(otherPlayer.getBoard());

                    List<Card> oppTaken = simulateParadeRemoval(paradeCopy2.getCards(), opponentCard);
                    tempOppBoard.addAll(oppTaken);

                    List<Colour> oppColours = decideMajority(otherPlayer, players, getPlayerBoardMaps(players));
                    int opponentScore = calculateScore(tempOppBoard, oppColours);
                    bestOpponentScore = Math.min(bestOpponentScore, opponentScore);
                }

                double delta = bestOpponentScore - currentScore;
                playerBestDeltas.add(delta);
            }

            double avgDelta = 0.0;
            
            for (double result : playerBestDeltas) {
                avgDelta += result;
            }

            avgDelta /= playerBestDeltas.size() - 1;

            if (avgDelta < bestDelta) {
                bestDelta = avgDelta;
                bestCard = candidateCard;
            }
        }

        return bestCard;
    }

    /**
     * Simulates playing a card and collecting the appropriate cards from the parade. Modifies the
     * parade by removing collected cards.
     */
    public List<Card> simulateParadeRemoval(List<Card> cards, Card placeCard) {

        List<Card> removedCards = new ArrayList<>();
    
        if (cards.size() > placeCard.getNumber()) {
            int removeZoneCardIndex = cards.size() - placeCard.getNumber();
    
            for (int i = 0; i < removeZoneCardIndex; i++) {
                Card cardAtIndex = cards.get(i);
                if (cardAtIndex.getNumber() <= placeCard.getNumber()
                        || cardAtIndex.getColour() == placeCard.getColour()) {
                    removedCards.add(cardAtIndex);
                }
            }
        }
    
        return removedCards;
    }
    

    /** Calculates the score of a board based on the card majority rules. */
    private int calculateScore(List<Card> board, List<Colour> majorityColour) {
        int score = 0;
        for (Card card : board) {
            if (majorityColour.contains(card.getColour())) {
                score += 1;
            } else {
                score += card.getNumber();
            }
        }
        return score;
    }

    /* Counts the number of each colour in the list of cards.
    */
    private Map<Colour, Integer> countColours(List<Card> cards) {
        Map<Colour, Integer> colourCount = new HashMap<>();

        if (cards == null) {
            throw new IllegalArgumentException("Card list cannot be null.");
        }

        for (Card card : cards) {
            if (card == null || card.getColour() == null) {
                throw new IllegalArgumentException("Card or card colour cannot be null.");
            }
            Colour colour = card.getColour();
            colourCount.put(colour, colourCount.getOrDefault(colour, 0) + 1);
        }
        return colourCount;
    }

    /**
     * Determines the majority colour(s) for a given player.
     */
    private List<Colour> decideMajority(Player targetPlayer, List<Player> allPlayers, 
                                        Map<Player, List<Card>> playerCards) {
        if (playerCards == null || targetPlayer == null) {
            throw new IllegalArgumentException("Player cards and target player cannot be null.");
        }

        if (!playerCards.containsKey(targetPlayer)) {
            throw new IllegalArgumentException("Target player is not present in player cards.");
        }

        List<Colour> targetMajorityColours = new ArrayList<>();
        Map<Colour, Integer> targetColourCounts = countColours(playerCards.get(targetPlayer));

        for (Map.Entry<Colour, Integer> colourEntry : targetColourCounts.entrySet()) {
            Colour colour = colourEntry.getKey();
            int targetCount = colourEntry.getValue();

            boolean isMajority = true;

            for (Player otherPlayer : allPlayers) {
                if (!otherPlayer.equals(targetPlayer)) {
                    int otherCount = countColours(playerCards.getOrDefault(otherPlayer, List.of()))
                                    .getOrDefault(colour, 0);
                    if ((allPlayers.size() == 2 && otherCount > targetCount - 2)
                            || (allPlayers.size() > 2 && otherCount > targetCount)) {
                        isMajority = false;
                        break;
                    }
                }
            }

            if (isMajority) {
                targetMajorityColours.add(colour);
            }
        }

        return targetMajorityColours;
    }

    private Map<Player, List<Card>> getPlayerBoardMaps(List<Player> players) {
        if (players == null) {
            throw new IllegalArgumentException ("Players cannot be null");
        }

        Map<Player, List<Card>> result = new HashMap<>();
        for (Player player : players) {
            result.put(player, player.getBoard());
        }
        return result;
    }

    @Override
    public Card discardCard(Player player, Parade parade) {
        // Randomly picks any card from the hand.
        int randIdx = new Random().nextInt(player.getHand().size());
        return player.getHand().get(randIdx);
    }

    @Override
    public String getName() {
        return "Hard Computer";
    }
}
