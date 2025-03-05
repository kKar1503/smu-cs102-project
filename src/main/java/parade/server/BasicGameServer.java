package parade.server;

import java.util.*;

import parade.common.Colour;
import parade.common.Deck;
import parade.common.Player;
import parade.utils.ScoreUtility;


public class BasicGameServer {
    private List<Player> playersList;  // List of players
    private Deck dk;  // The deck
    private Map<Player, Integer> playerScores; // Track player scores
    private List<Card> parade; // Cards currently in the parade

    // Constructor to initialize the game server with a deck
    
    public BasicGameServer(Deck dk) {
        this.playersList = new ArrayList<>();
        this.dk = dk;
        this.playerScores = new HashMap<>();
        this.parade = new ArrayList<>();
    }

    // Add a player to the game
    public void addPlayer(Player p) {
        playersList.add(p);
        playerScores.put(p, 0);  // Initialize player's score to 0
    }

    // Start the game
    public void startGame() {
        // Continue until the deck is empty or there's a winner
        while (dk.size() > 0) {
            // Draw cards for each player
            for (Player player : playersList) {
                Card drawnCard = dk.draw();
                if (drawnCard != null) {
                    player.draw(drawnCard);
                    System.out.println(player.getName() + " drew: " + drawnCard);
                }
            }

            // Show the current parade
            System.out.println("Current Parade: " + parade);

            // Each player plays their card (could be the first card or based on the game's logic)
            for (Player player : playersList) {
                Card playedCard = player.playCard(parade);
                if (playedCard != null) {
                    parade.add(playedCard);
                    System.out.println(player.getName() + " played: " + playedCard);
                }
            }

            // Get the majority colors for each player using ScoreUtility
            Map<Player, Colour> majorityColours = ScoreUtility.getMajority(getPlayerHands());

            // Calculate scores for each player using ScoreUtility
            for (Player player : playersList) {
                Colour majorityColour = majorityColours.get(player);
                int score = ScoreUtility.calculateScore(player.getHand(), majorityColour);
                playerScores.put(player, playerScores.get(player) + score);
                System.out.println(player.getName() + "'s score: " + playerScores.get(player));
            }

            // Check for game-ending conditions
            if (isGameOver()) {
                break;
            }
        }

        // Print the final scores and declare the winner
        System.out.println("Game Over! Final Scores:");
        declareWinner();
    }

    // Check if the game is over (deck is empty or someone has won)
    private boolean isGameOver() {
        // For example, a game ends when the deck is empty
        return dk.size() == 0;
    }

    // Declare the winner based on the highest score
    private void declareWinner() {
        Player winner = null;
        int highestScore = Integer.MIN_VALUE;

        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (winner != null) {
            System.out.println("Winner: " + winner.getName() + " with " + highestScore + " points!");
        } else {
            System.out.println("The game ended in a tie!");
        }
    }

    // Get hands of all players
    private Map<Player, List<Card>> getPlayerHands() {
        Map<Player, List<Card>> playerHands = new HashMap<>();
        for (Player player : playersList) {
            playerHands.put(player, player.getHand());
        }
        return playerHands;
    }
}

