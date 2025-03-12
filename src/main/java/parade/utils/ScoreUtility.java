package parade.utils;

import parade.common.Card;
import parade.common.Colour;
import parade.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreUtility {

    // Helper function to count occurrences of each colour
    private static Map<Colour, Integer> countColours(List<Card> cards) {
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

    // Method to determine majority colours for a specific player
    public static Map<Player, List<Colour>> decideMajority(
            Map<Player, List<Card>> playerCards, Player targetPlayer) {

        if (playerCards == null || targetPlayer == null) {
            throw new IllegalArgumentException("Player cards and target player cannot be null.");
        }

        if (!playerCards.containsKey(targetPlayer)) {
            throw new IllegalArgumentException("Target player is not present in player cards.");
        }

        Map<Player, List<Colour>> majorityColours = new HashMap<>();
        List<Colour> targetMajorityColours = new ArrayList<>();

        // Step 1: Count occurrences of each colour for the target player
        Map<Colour, Integer> targetColourCounts = countColours(playerCards.get(targetPlayer));

        // Step 2: Compare against all other players
        for (Map.Entry<Colour, Integer> colourEntry : targetColourCounts.entrySet()) {
            Colour colour = colourEntry.getKey();
            int targetCount = colourEntry.getValue();

            boolean isMajority = true; // Assume majority until proven otherwise

            for (Map.Entry<Player, List<Card>> entry : playerCards.entrySet()) {
                Player otherPlayer = entry.getKey();
                if (!otherPlayer.equals(targetPlayer)) { // Don't compare against self
                    int otherCount = countColours(entry.getValue()).getOrDefault(colour, 0);
                    if ((playerCards.size() == 2 && otherCount > targetCount - 2)
                            || // 2 player mode (majority -> 2 or more cards)
                            (playerCards.size() > 2
                                    && otherCount > targetCount)) { // multiplayer mode
                        isMajority = false;
                        break;
                    }
                }
            }

            // If target player holds a majority in this colour, add it
            if (isMajority) {
                targetMajorityColours.add(colour);
            }
        }

        // Ensure the player has an empty list instead of null if they have no majority
        majorityColours.put(targetPlayer, targetMajorityColours);

        return majorityColours;
    }

    // Method to calculate the score of a player
    public static int calculateScore(
            Player targetPlayer,
            Map<Player, List<Card>> playerCards,
            Map<Player, List<Colour>> majorityColours) {
        int score = 0;
        List<Card> playerCardList = playerCards.get(targetPlayer);
        List<Colour> playerMajorityColours =
                majorityColours.getOrDefault(targetPlayer, new ArrayList<>());

        for (Card card : playerCardList) {
            // Only add the value of the card if it's NOT in the player's majority colours
            if (!playerMajorityColours.contains(card.getColour())) {
                score += card.getNumber();
            } else {
                score += 1;
            }
        }

        return score;
    }
}
