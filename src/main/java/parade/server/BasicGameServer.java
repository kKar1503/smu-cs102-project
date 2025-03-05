package parade.server;

import parade.common.*;
import parade.player.Human;
import parade.player.Player;
import parade.textrenderer.DebugRendererProvider;
import parade.textrenderer.TextRendererProvider;
import parade.utils.ScoreUtility;

import java.util.*;

/**
 * Represents the game server for the Parade game. Manages players, the deck, the parade, and game
 * flow.
 */
public class BasicGameServer implements Server {
    private final List<Player> playersList; // List of players in the game
    private final Deck deck; // The deck of cards used in the game
    private final Map<Player, Integer> playerScores; // Stores each player's score
    private final Parade parade; // The list of cards currently in the parade

    /** Initializes the game server with a deck. */
    public BasicGameServer() {
        this.playersList = new ArrayList<>();
        this.deck = new Deck();
        this.playerScores = new HashMap<>();
        List<Card> parade_cards = new ArrayList<Card>();
        for (int i = 0; i < 6; i++) {
            parade_cards.add(deck.drawCard());
        }
        this.parade = new Parade(parade_cards);
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

    @Override
    public void waitForPlayersLobby() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            TextRendererProvider.getInstance().renderPlayersLobby(playersList);
            int input = scanner.nextInt();
            scanner.nextLine();
            if (input == 1) {
                if (playersList.size() == 6) {
                    TextRendererProvider.getInstance().render("Lobby is full.");
                    continue;
                }
                System.out.print("Enter player name: ");
                String name = scanner.nextLine();
                addPlayer(new Human(name));
            } else if (input == 2) {
                return;
            }
        }
    }

    /**
     * Starts the game loop and manages game progression.
     *
     * @throws IllegalStateException if there are less than 2 players
     */
    @Override
    public void startGame() throws IllegalStateException {
        if (playersList.size() < 2) {
            throw new IllegalStateException("Server requires at least two players");
        }
        // Game loop continues until the deck is empty or an end condition is met
        while (!deck.isDeckEmpty() && !shouldGameEnd()) {
            System.out.println("here");
            // Each player plays a card
            for (Player player : playersList) {
                Card drawnCard = deck.drawCard();
                if (drawnCard != null) {
                    DebugRendererProvider.getInstance()
                            .debugf("%s drew: %s", player.getName(), drawnCard);
                    player.draw(drawnCard);
                }

                DebugRendererProvider.getInstance().debugf("%s playing a card", player.getName());
                Card playedCard = player.playCard(parade.getParadeCards());
                if (playedCard != null) {
                    DebugRendererProvider.getInstance()
                            .debugf(
                                    "%s played and placed card into parade: %s",
                                    player.getName(), playedCard);
                    parade.placeCard(playedCard); // Apply parade logic
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
            Card playedCard = player.playCard(parade.getParadeCards());
            if (playedCard != null) {
                parade.placeCard(playedCard);
                System.out.println(player.getName() + " played: " + playedCard);
            }
        }

        // Declare the final results
        TextRendererProvider.getInstance().render("Game Over! Final Scores:");
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
            TextRendererProvider.getInstance()
                    .render("Winner: " + winner.getName() + " with " + lowestScore + " points!");
        } else {
            TextRendererProvider.getInstance().render("The game ended in a tie!");
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
        return deck.isDeckEmpty(); // Game ends when the deck is empty
    }
}

// check if anybody has all 6 colours or deck is empty. when this happens
// everybody has one more turn (one more round played) but they do NOT draw a card.
// after this round the game ends (everybody either ways ends with 4 cards)
