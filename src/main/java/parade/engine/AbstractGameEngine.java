package parade.engine;

import parade.common.Card;
import parade.common.Colour;
import parade.common.Deck;
import parade.common.Parade;
import parade.player.IPlayer;

import java.util.*;

public abstract class AbstractGameEngine {
    public static final int MIN_PLAYERS = 2; // Minimum number of players required to start the game
    public static final int MAX_PLAYERS = 6; // Maximum number of players allowed
    public static final int INITIAL_CARDS_PER_PLAYER = 5; // Number of cards each player starts with
    public static final int PARADE_SIZE = 6; // Number of cards in the parade

    private final List<IPlayer> players = new ArrayList<>(); // List of players in the game
    private final Deck deck = new Deck(); // The deck of cards used in the game
    private final Parade parade; // The list of cards currently in the parade
    private final Lobby lobby;

    protected AbstractGameEngine() {
        List<Card> parade_cards = new ArrayList<>(deck.pop(PARADE_SIZE));
        parade = new Parade(parade_cards);
        lobby = new Lobby(players);
    }

    /**
     * Adds a player to the game.
     *
     * @param player The player to be added.
     */
    public void addPlayer(IPlayer player) {
        lobby.getPlayers().add(player);
    }

    /**
     * Removes a player from the game.
     *
     * @param player The player to be removed.
     */
    public boolean removePlayer(IPlayer player) {
        return lobby.getPlayers().remove(player);
    }

    /**
     * Removes a player from the game.
     *
     * @param index The index of the player to be removed.
     */
    public IPlayer removePlayer(int index) {
        return lobby.getPlayers().remove(index);
    }

    /**
     * Gets the list of players in the game.
     *
     * @return An unmodifiable copy of the list of players.
     */
    protected List<IPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Gets the player at the specified index.
     *
     * @param index The index of the player.
     * @return The player at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    protected IPlayer getPlayer(int index) throws IndexOutOfBoundsException {
        return players.get(index);
    }

    /**
     * Gets the current player by delegating to the Lobby.
     *
     * @return The current player.
     */
    protected IPlayer getCurrentPlayer() {
        return lobby.getCurrentPlayer();
    }

    /**
     * Sets the current player to be a specific index
     * 
     * @param idx The index of the player
     */
    protected void setCurrentPlayer(int idx) {
        lobby.setCurrentPlayer(idx);
    }

    /** Advances to the next player by delegating to the Lobby. */
    protected void nextPlayer() {
        lobby.nextPlayer();
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
     * Checks if the lobby is empty.
     *
     * @return True if the lobby is empty, false otherwise.
     */
    protected boolean isLobbyEmpty() {
        return players.isEmpty();
    }

    /**
     * Checks if the lobby has enough players to start the game.
     *
     * @return True if the lobby has enough players, false otherwise.
     */
    protected boolean lobbyHasEnoughPlayers() {
        return players.size() >= MIN_PLAYERS;
    }

    public abstract void start();

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
        return deck.pop();
    }

    /**
     * Draws n card from the deck. @Param n The number of cards to draw.
     *
     * @return The drawn card.
     */
    protected List<Card> drawFromDeck(int n) {
        return deck.pop(n);
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
        for (IPlayer player : players) {
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

    protected Map<IPlayer, Integer> tabulateScores() {
        Map<IPlayer, List<Card>> playerBoards = new HashMap<>();
        for (IPlayer player : players) {
            playerBoards.put(player, player.getBoard());
        }

        // Calculate majority colours for each player
        Map<IPlayer, List<Colour>> majorityColours = new HashMap<>();
        for (IPlayer player : players) {
            majorityColours.put(player, decideMajority(playerBoards, player).get(player));
        }

        Map<IPlayer, Integer> playerScores = new HashMap<>();
        // Calculate scores for each player
        for (IPlayer player : players) {
            int score = calculateScore(player, playerBoards, majorityColours);
            playerScores.put(player, score);
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
     * Determines the majority colours for a specific player based on their card collection compared
     * to other players.
     *
     * <p>In a two-player game, a player holds a majority if they have at least two more cards of a
     * particular colour than their opponent. In multiplayer, a player must simply have more cards
     * of a particular colour than any other player.
     *
     * @param playerCards A map where the key is a {@link Player} and the value is their list of
     *     cards. Must not be null.
     * @param targetPlayer The player for whom to determine majority colours. Must not be null and
     *     must exist in {@code playerCards}.
     * @return A map where the key is the {@link Player} and the value is a list of {@link Colour}
     *     where they hold a majority.
     * @throws IllegalArgumentException If {@code playerCards} or {@code targetPlayer} is null, or
     *     if the target player is not present in the map.
     */
    public Map<IPlayer, List<Colour>> decideMajority(
            Map<IPlayer, List<Card>> playerCards, IPlayer targetPlayer) {

        if (playerCards == null || targetPlayer == null) {
            throw new IllegalArgumentException("Player cards and target player cannot be null.");
        }

        if (!playerCards.containsKey(targetPlayer)) {
            throw new IllegalArgumentException("Target player is not present in player cards.");
        }

        Map<IPlayer, List<Colour>> majorityColours = new HashMap<>();
        List<Colour> targetMajorityColours = new ArrayList<>();

        // Step 1: Count occurrences of each colour for the target player
        Map<Colour, Integer> targetColourCounts = countColours(playerCards.get(targetPlayer));

        // Step 2: Compare against all other players
        for (Map.Entry<Colour, Integer> colourEntry : targetColourCounts.entrySet()) {
            Colour colour = colourEntry.getKey();
            int targetCount = colourEntry.getValue();

            boolean isMajority = true; // Assume majority until proven otherwise

            for (Map.Entry<IPlayer, List<Card>> entry : playerCards.entrySet()) {
                IPlayer otherPlayer = entry.getKey();
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

    /**
     * Calculates the score of a player based on their cards and majority colours.
     *
     * <p>If a card's colour is among the player's majority colours, its score is counted as 1.
     * Otherwise, the card's actual number value is added to the score.
     *
     * @param targetPlayer The player whose score is to be calculated. Must not be null.
     * @param playerCards A map where the key is a {@link Player} and the value is their list of
     *     cards. Must not be null.
     * @param majorityColours A map where the key is a {@link Player} and the value is a list of
     *     {@link Colour} that are majority for that player. Must not be null.
     * @return The calculated score of the target player.
     * @throws IllegalArgumentException If any argument is null, or if the target player does not
     *     have a card list.
     */
    public int calculateScore(
            IPlayer targetPlayer,
            Map<IPlayer, List<Card>> playerBoards,
            Map<IPlayer, List<Colour>> majorityColours) {
        int score = 0;
        if (targetPlayer == null || playerBoards == null || majorityColours == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        List<Card> playerBoardList = playerBoards.get(targetPlayer);
        if (playerBoardList == null) {
            throw new IllegalArgumentException("Target player has no card list.");
        }

        List<Colour> playerMajorityColours =
                majorityColours.get(targetPlayer);

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
