package parade.common;

import parade.player.Player;
import parade.utils.ScoreUtility;

import java.util.*;

public abstract class Server {
    public static final int MIN_PLAYERS = 2; // Minimum number of players required to start the game
    public static final int MAX_PLAYERS = 6; // Maximum number of players allowed
    public static final int INITIAL_CARDS_PER_PLAYER = 4; // Number of cards each player starts with
    public static final int PARADE_SIZE = 6; // Number of cards in the parade

    private final List<Player> players = new ArrayList<>(); // List of players in the game
    private final Deck deck = new Deck(); // The deck of cards used in the game
    private final Parade parade; // The list of cards currently in the parade

    private int currentPlayerIdx = 0; // The index of the current player

    protected Server() {
        List<Card> parade_cards = new ArrayList<>(deck.draw(PARADE_SIZE));
        parade = new Parade(parade_cards);
    }

    /**
     * Adds a player to the game.
     *
     * @param player The player to be added.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Gets the list of players in the game.
     *
     * @return An unmodifiable copy of the list of players.
     */
    protected List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Gets the player at the specified index.
     *
     * @param index The index of the player.
     * @return The player at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    protected Player getPlayer(int index) throws IndexOutOfBoundsException {
        return players.get(index);
    }

    /**
     * Gets the current player.
     *
     * @return The current player.
     */
    protected Player getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    /** Increments the index of the current player to the next player in the list. */
    protected void nextPlayer() {
        currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
    }

    /**
     * Get the number of players in the game
     *
     * @return The number of players in the game
     */
    protected int getPlayersCount() {
        return players.size();
    }

    /**
     * Checks if the lobby is full.
     *
     * @return True if the lobby is full, false otherwise.
     */
    protected boolean isLobbyFull() {
        return players.size() == MAX_PLAYERS;
    }

    /**
     * Checks if the lobby has enough players to start the game.
     *
     * @return True if the lobby has enough players, false otherwise.
     */
    protected boolean lobbyHasEnoughPlayers() {
        return players.size() >= MIN_PLAYERS;
    }

    public abstract void startGame();

    public abstract void waitForPlayersLobby();

    /**
     * Determines if the deck is empty.
     *
     * @return True if the deck is empty, false otherwise.
     */
    protected boolean isDeckEmpty() {
        return deck.isEmpty();
    }

    /**
     * Draws a card from the deck.
     *
     * @return The drawn card.
     */
    protected Card drawFromDeck() {
        return deck.draw();
    }

    /**
     * Draws n card from the deck. @Param n The number of cards to draw.
     *
     * @return The drawn card.
     */
    protected List<Card> drawFromDeck(int n) {
        return deck.draw(n);
    }

    /**
     * Places a card in the parade and returns the card that is received from the parade.
     *
     * @return The card that is received from the parade.
     */
    protected List<Card> placeCardInParade(Card card) {
        return parade.placeCard(card);
    }

    /**
     * Gets the list of cards in the parade.
     *
     * @return An unmodifiable copy of the list of cards in the parade.
     */
    protected List<Card> getParadeCards() {
        return parade.getCards();
    }

    /**
     * Checks if any player has collected all colours or if the deck is empty. When this happens,
     * the game enters a final phase where players play one more round without drawing a card. After
     * this round, the game ends and all players are left with 4 cards.
     *
     * @return True if game should continue, false otherwise.
     */
    protected boolean shouldGameContinue() {
        if (isDeckEmpty()) {
            // Game ends when the deck is empty
            return false;
        }
        for (Player player : players) {
            Set<Colour> uniqueColours = new HashSet<>();
            for (Card card : player.getBoard()) {
                uniqueColours.add(card.getColour());
            }
            if (uniqueColours.size() == Colour.values().length) {
                return false; // A player has all 6 colours
            }
        }
        return true;
    }

    protected Map<Player, Integer> tabulateScores() {
        Map<Player, List<Card>> playerHands = new HashMap<>();
        for (Player player : players) {
            playerHands.put(player, player.getHand());
        }

        // Calculate majority colours for each player
        Map<Player, List<Colour>> majorityColours = new HashMap<>();
        for (Player player : players) {
            majorityColours.put(
                    player, ScoreUtility.decideMajority(playerHands, player).get(player));
        }

        Map<Player, Integer> playerScores = new HashMap<>();
        // Calculate scores for each player
        for (Player player : players) {
            int score = ScoreUtility.calculateScore(player, playerHands, majorityColours);
            playerScores.put(player, score);
        }

        return playerScores;
    }
}
