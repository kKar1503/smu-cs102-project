package parade.core;

import parade.card.Card;
import parade.card.Colour;
import parade.card.Deck;
import parade.card.Parade;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;

import java.util.*;

abstract class AbstractGameEngine {
    static final int INITIAL_CARDS_PER_PLAYER = 4; // Number of cards each player starts with
    static final int PARADE_SIZE = 6; // Number of cards in the parade

    final Deck deck = new Deck(); // The deck of cards used in the game
    final PlayerControllerManager playerControllerManager;
    final Parade parade; // The list of cards currently in the parade

    AbstractGameEngine() {
        playerControllerManager = new PlayerControllerManager();
        List<Card> parade_cards = new ArrayList<>(deck.pop(PARADE_SIZE));
        parade = new Parade(parade_cards);
    }

    public abstract void start();

    /**
     * Checks if any player has collected all colours or if the deck is empty. When this happens,
     * the game enters a final phase where players play one more round without drawing a card. After
     * this round, the game ends and all players are left with 4 cards.
     *
     * @return True if game should continue, false otherwise.
     */
    boolean shouldGameContinue() {
        if (deck.isEmpty()) {
            // Game ends when the deck is empty
            return false;
        }
        for (AbstractPlayerController player : playerControllerManager.getPlayerControllers()) {
            Set<Colour> uniqueColours = new HashSet<>();
            for (Card card : player.getPlayer().getBoard()) {
                uniqueColours.add(card.getColour());
            }
            if (uniqueColours.size() == Colour.values().length) {
                return false; // A player has all 6 colours
            }
        }
        return true;
    }

    Map<AbstractPlayerController, Integer> tabulateScores() {
        List<AbstractPlayerController> controllers = playerControllerManager.getPlayerControllers();

        Map<AbstractPlayerController, List<Card>> playerBoards = new HashMap<>();
        for (AbstractPlayerController controller : controllers) {
            playerBoards.put(controller, controller.getPlayer().getBoard());
        }

        // Calculate majority colours for each player
        Map<AbstractPlayerController, List<Colour>> majorityColours = new HashMap<>();
        for (AbstractPlayerController controller : controllers) {
            majorityColours.put(
                    controller, decideMajority(playerBoards, controller).get(controller));
        }

        Map<AbstractPlayerController, Integer> playerScores = new HashMap<>();
        // Calculate scores for each player
        for (AbstractPlayerController controller : controllers) {
            int score = calculateScore(controller, playerBoards, majorityColours);
            playerScores.put(controller, score);
        }

        return playerScores;
    }

    /**
     * Counts the occurrences of each colour in a list of cards.
     *
     * @param cards The list of cards to process. Must not be null, and each card and its colour
     *     must also not be null.
     * @return A map where the key is the {@link Colour} and the value is the count of its
     *     occurrences.
     * @throws IllegalArgumentException If the card list is null or contains null cards or colours.
     */
    Map<Colour, Integer> countColours(List<Card> cards) {
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
     * Determines the majority colours for a specific player based on their card collection compared
     * to other players.
     *
     * <p>In a two-player game, a player holds a majority if they have at least two more cards of a
     * particular colour than their opponent. In multiplayer, a player must simply have more cards
     * of a particular colour than any other player.
     *
     * @param playerCards A map where the key is a {@link Player} and the value is their list of
     *     cards. Must not be null.
     * @param targetPlayerController The player for whom to determine majority colours. Must not be
     *     null and must exist in {@code playerCards}.
     * @return A map where the key is the {@link Player} and the value is a list of {@link Colour}
     *     where they hold a majority.
     * @throws IllegalArgumentException If {@code playerCards} or {@code targetPlayer} is null, or
     *     if the target player is not present in the map.
     */
    Map<AbstractPlayerController, List<Colour>> decideMajority(
            Map<AbstractPlayerController, List<Card>> playerCards,
            AbstractPlayerController targetPlayerController) {

        Player targetPlayer = targetPlayerController.getPlayer();

        if (playerCards == null || targetPlayer == null) {
            throw new IllegalArgumentException("Player cards and target player cannot be null.");
        }

        if (!playerCards.containsKey(targetPlayerController)) {
            throw new IllegalArgumentException("Target player is not present in player cards.");
        }

        Map<AbstractPlayerController, List<Colour>> majorityColours = new HashMap<>();
        List<Colour> targetMajorityColours = new ArrayList<>();

        // Step 1: Count occurrences of each colour for the target player
        Map<Colour, Integer> targetColourCounts =
                countColours(playerCards.get(targetPlayerController));

        // Step 2: Compare against all other players
        for (Map.Entry<Colour, Integer> colourEntry : targetColourCounts.entrySet()) {
            Colour colour = colourEntry.getKey();
            int targetCount = colourEntry.getValue();

            boolean isMajority = true; // Assume majority until proven otherwise

            for (Map.Entry<AbstractPlayerController, List<Card>> entry : playerCards.entrySet()) {
                Player otherPlayer = entry.getKey().getPlayer();
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
        majorityColours.put(targetPlayerController, targetMajorityColours);

        return majorityColours;
    }

    /**
     * Calculates the score of a player based on their cards and majority colours.
     *
     * <p>If a card's colour is among the player's majority colours, its score is counted as 1.
     * Otherwise, the card's actual number value is added to the score.
     *
     * @param targetPlayerController The player whose score is to be calculated. Must not be null.
     * @param playerCards A map where the key is a {@link Player} and the value is their list of
     *     cards. Must not be null.
     * @param majorityColours A map where the key is a {@link Player} and the value is a list of
     *     {@link Colour} that are majority for that player. Must not be null.
     * @return The calculated score of the target player.
     * @throws IllegalArgumentException If any argument is null, or if the target player does not
     *     have a card list.
     */
    public int calculateScore(
            AbstractPlayerController targetPlayer,
            Map<AbstractPlayerController, List<Card>> playerBoards,
            Map<AbstractPlayerController, List<Colour>> majorityColours) {
        int score = 0;
        if (targetPlayer == null || playerBoards == null || majorityColours == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        List<Card> playerBoardList = playerBoards.get(targetPlayer);
        if (playerBoardList == null) {
            throw new IllegalArgumentException("Target player has no card list.");
        }

        List<Colour> playerMajorityColours = majorityColours.get(targetPlayer);

        for (Card card : playerBoardList) {
            if (card == null || card.getColour() == null) {
                throw new IllegalArgumentException("Card or card colour cannot be null.");
            }
            // Only add the value of the card if it's NOT in the player's majority colours
            if (playerMajorityColours.contains(card.getColour())) {
                score += 1;
            } else {
                score += card.getNumber();
            }
        }

        return score;
    }
}
