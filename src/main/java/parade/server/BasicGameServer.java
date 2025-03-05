package parade.server;

import parade.common.Card;
import parade.common.Colour;
import parade.common.Deck;
import parade.player.Player;
import parade.utils.ScoreUtility;

import java.util.*;

/**
 * Represents the game server for the Parade game. Manages players, the deck, the parade, and game
 * flow.
 */
public class BasicGameServer {
    private List<Player> playersList; // List of players in the game
    private Deck deck; // The deck of cards used in the game
    private Map<Player, Integer> playerScores; // Stores each player's score
    private List<Card> parade; // The list of cards currently in the parade

    /**
     * Initializes the game server with a deck.
     *
     * @param deck The deck to be used in the game.
     */
    public BasicGameServer(Deck deck) {
        this.playersList = new ArrayList<>();
        this.deck = deck;
        this.playerScores = new HashMap<>();
        this.parade = new ArrayList<>();
    }

    /**
     * Adds a player to the game.
     *
     * @param p The player to be added.
     */
    public void addPlayer(Player p) {
        playersList.add(p);
        playerScores.put(p, 0); // Initialize player's score to 0
    }

    /** Starts the game loop and manages game progression. */
    public void startGame() {
        // Game loop continues until the deck is empty or an end condition is met
        while (!deck.isDeckEmpty() && !shouldGameEnd()) {
            // Players draw cards only in normal rounds
            for (Player player : playersList) {
                Card drawnCard = deck.drawCard();
                if (drawnCard != null) {
                    player.draw(drawnCard);
                    System.out.println(player.getName() + " drew: " + drawnCard);
                }
            }

            // Display the current parade state
            System.out.println("Current Parade: " + parade);

            // Each player plays a card
            for (Player player : playersList) {
                Card playedCard = player.playCard(parade);
                if (playedCard != null) {
                    parade.placeCard(playedCard, parade); // Apply parade logic
                    System.out.println(player.getName() + " played: " + playedCard);
                }
            }

            // Calculate majority colours for each player
            Map<Player, List<Card>> playerHands = getPlayerHands();
            Map<Player, List<Colour>> majorityColours = new HashMap<>();

            for (Player player : playersList) {
                majorityColours.put(
                        player, ScoreUtility.decideMajority(playerHands, player).get(player));
            }

            // Calculate scores for each player
            for (Player player : playersList) {
                int score = ScoreUtility.calculateScore(player, playerHands, majorityColours);
                playerScores.put(player, playerScores.get(player) + score);
                System.out.println(player.getName() + "'s score: " + playerScores.get(player));
            }
        }

        // After the game loop finishes, check if the extra round is needed.
        // Allow players to play one more round even if the deck is empty or the end condition is
        // met
        System.out.println("Extra round started. Players do not draw a card.");
        for (Player player : playersList) {
            Card playedCard = player.playCard(parade);
            if (playedCard != null) {
                player.placeCard(playedCard, parade);
                System.out.println(player.getName() + " played: " + playedCard);
            }
        }

        // Declare the final results
        System.out.println("Game Over! Final Scores:");
        declareWinner();
    }

    /** Declares the winner based on the lowest score. */
    private void declareWinner() {
        Player winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() < lowestScore) {
                lowestScore = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (winner != null) {
            System.out.println("Winner: " + winner.getName() + " with " + lowestScore + " points!");
        } else {
            System.out.println("The game ended in a tie!");
        }
    }

    /**
     * Retrieves the hands of all players.
     *
     * @return A map containing each player and their respective hand of cards.
     */
    private Map<Player, List<Card>> getPlayerHands() {
        Map<Player, List<Card>> playerHands = new HashMap<>();
        for (Player player : playersList) {
            playerHands.put(player, player.getHand());
        }
        return playerHands;
    }

    /**
     * Checks if any player has collected all 6 colours or if the deck is empty.
     *
     * @return True if an end condition is met, false otherwise.
     */
    private boolean shouldGameEnd() {
        for (Player player : playersList) {
            Set<Colour> uniqueColours = new HashSet<>();
            for (Card card : player.getHand()) {
                uniqueColours.add(card.getColour());
            }
            if (uniqueColours.size() == 6) {
                return true; // A player has all 6 colours
            }
        }
        return deck.size() == 0; // Game ends when the deck is empty
    }
}

// check if anybody has all 6 colours or deck is empty. when this happens
// everybody has one more turn (one more round played) but they do NOT draw a card.
// after this round the game ends (everybody either ways ends with 4 cards)
