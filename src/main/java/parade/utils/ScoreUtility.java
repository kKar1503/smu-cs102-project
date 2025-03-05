package parade.utils;

import parade.common.Card;
import parade.common.Colour;
import parade.common.Player;

import java.util.List;
import java.util.Map;

public class ScoreUtility {

    // Helper function to count occurrences of each colour
    private static Map<Colour, Integer> countColours(List<Card> cards) {
        Map<Colour, Integer> colourCount = new HashMap<>();
        for (Card card : cards) {
            Colour colour = card.getColour();
            colourCount.put(colour, colourCount.getOrDefault(colour, 0) + 1);
        }
        return colourCount;
    }

    // Method to determine majority colours for a specific player
    public static Map<Player, List<Colour>> decideMajority(
            Map<Player, List<Card>> playerCards, Player targetPlayer) {
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
                    if (otherCount > targetCount) {
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
