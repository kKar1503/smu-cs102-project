package parade.engine;

import parade.common.Card;
import parade.common.Colour;
import parade.common.Deck;
import parade.common.Parade;
import parade.common.Player;
import parade.controller.IPlayerController;

import java.util.*;

public abstract class AbstractGameEngine<T extends IPlayerController> {
    public static final int MIN_PLAYERS = 2; // Minimum number of players required to start the game
    public static final int MAX_PLAYERS = 6; // Maximum number of players allowed
    public static final int INITIAL_CARDS_PER_PLAYER = 4; // Number of cards each player starts with
    public static final int PARADE_SIZE = 6; // Number of cards in the parade

    private final Deck deck = new Deck(); // The deck of cards used in the game
    private final Lobby<T> lobby = new Lobby<>();
    private final Parade parade; // The list of cards currently in the parade

    protected AbstractGameEngine() {
        List<Card> parade_cards = new ArrayList<>(deck.pop(PARADE_SIZE));
        parade = new Parade(parade_cards);
    }

    /**
     * Adds a player controller to the game.
     *
     * @param playerController The player controller to be added.
     */
    public void addPlayerController(T playerController) {
        lobby.getPlayerControllers().add(playerController);
    }

    /**
     * Removes a player controller from the game.
     *
     * @param player The player controller to be removed.
     */
    public boolean removePlayer(T player) {
        return lobby.getPlayerControllers().remove(player);
    }

    /**
     * Removes a player controller from the game.
     *
     * @param index The index of the player controller to be removed.
     */
    public T removePlayer(int index) {
        return lobby.getPlayerControllers().remove(index);
    }

    /**
     * Gets the list of players in the game.
     *
     * @return An unmodifiable copy of the list of players.
     */
    protected List<Player> getPlayers() {
        return lobby.getPlayerControllers().stream().map(T::getPlayer).toList();
    }

    /**
     * Gets the player at the specified index.
     *
     * @param index The index of the player.
     * @return The player at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    protected T getPlayer(int index) throws IndexOutOfBoundsException {
        return lobby.getPlayerControllers().get(index);
    }

    /**
     * Gets the current player by delegating to the Lobby.
     *
     * @return The current player.
     */
    protected T getCurrentPlayer() {
        return lobby.getPlayerControllers().stream()
                .filter(p -> p.equals(lobby.getCurrentPlayerController()))
                .findFirst()
                .orElse(null);
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
        return lobby.getPlayerControllers().size();
    }

    /**
     * Checks if the lobby is full.
     *
     * @return True if the lobby is full, false otherwise.
     */
    protected boolean isLobbyFull() {
        return lobby.getPlayerControllers().size() == MAX_PLAYERS;
    }

    /**
     * Checks if the lobby is empty.
     *
     * @return True if the lobby is empty, false otherwise.
     */
    protected boolean isLobbyEmpty() {
        return lobby.getPlayerControllers().isEmpty();
    }

    /**
     * Checks if the lobby has enough players to start the game.
     *
     * @return True if the lobby has enough players, false otherwise.
     */
    protected boolean lobbyHasEnoughPlayers() {
        return lobby.getPlayerControllers().size() >= MIN_PLAYERS;
    }

    public abstract void start();

    /**
     * Gets the size of the deck.
     *
     * @return The size of the deck.
     */
    protected int getDeckSize() {
        return deck.size();
    }

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
        for (IPlayerController player : lobby.getPlayerControllers()) {
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

    protected Map<T, Integer> tabulateScores() {
        Map<T, List<Card>> playerHands = new HashMap<>();
        for (T player : lobby.getPlayerControllers()) {
            playerHands.put(player, player.getPlayer().getHand());
        }

        // Calculate majority colours for each player
        Map<T, List<Colour>> majorityColours = new HashMap<>();
        for (T player : lobby.getPlayerControllers()) {
            majorityColours.put(player, decideMajority(playerHands, player).get(player));
        }

        Map<T, Integer> playerScores = new HashMap<>();
        // Calculate scores for each player
        for (T player : lobby.getPlayerControllers()) {
            int score = calculateScore(player, playerHands, majorityColours);
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
     * @param targetPlayerController The player for whom to determine majority colours. Must not be
     *     null and must exist in {@code playerCards}.
     * @return A map where the key is the {@link Player} and the value is a list of {@link Colour}
     *     where they hold a majority.
     * @throws IllegalArgumentException If {@code playerCards} or {@code targetPlayer} is null, or
     *     if the target player is not present in the map.
     */
    public Map<T, List<Colour>> decideMajority(
            Map<T, List<Card>> playerCards, T targetPlayerController) {

        Player targetPlayer = targetPlayerController.getPlayer();

        if (playerCards == null || targetPlayer == null) {
            throw new IllegalArgumentException("Player cards and target player cannot be null.");
        }

        if (!playerCards.containsKey(targetPlayerController)) {
            throw new IllegalArgumentException("Target player is not present in player cards.");
        }

        Map<T, List<Colour>> majorityColours = new HashMap<>();
        List<Colour> targetMajorityColours = new ArrayList<>();

        // Step 1: Count occurrences of each colour for the target player
        Map<Colour, Integer> targetColourCounts =
                countColours(playerCards.get(targetPlayerController));

        // Step 2: Compare against all other players
        for (Map.Entry<Colour, Integer> colourEntry : targetColourCounts.entrySet()) {
            Colour colour = colourEntry.getKey();
            int targetCount = colourEntry.getValue();

            boolean isMajority = true; // Assume majority until proven otherwise

            for (Map.Entry<T, List<Card>> entry : playerCards.entrySet()) {
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
            T targetPlayerController,
            Map<T, List<Card>> playerCards,
            Map<T, List<Colour>> majorityColours) {
        int score = 0;
        if (targetPlayerController == null || playerCards == null || majorityColours == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        List<Card> playerCardList = playerCards.get(targetPlayerController);
        if (playerCardList == null) {
            throw new IllegalArgumentException("Target player has no card list.");
        }

        List<Colour> playerMajorityColours =
                majorityColours.getOrDefault(targetPlayerController, new ArrayList<>());

        for (Card card : playerCardList) {
            if (card == null || card.getColour() == null) {
                throw new IllegalArgumentException("Card or card colour cannot be null.");
            }

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
