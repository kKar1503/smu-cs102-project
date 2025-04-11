package parade.player;

import parade.card.Card;

import java.util.*;

public class Player {
    // Name needs to be unique as it's used for identification
    private String name;
    private final List<Card> hand;
    private final List<Card> board;

    public Player(String name) {
        this.name = PlayerNameRegistry.getUniqueName(name);
        this.hand = new LinkedList<>();
        this.board = new ArrayList<>();
    }

    // This is a Copy constructor, useful for creating a new player with the same state as an
    // existing one. For @Greg usage to write his Computer simulation logic.
    public Player(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.name = player.name;
        this.hand = new LinkedList<>(player.hand);
        this.board = new ArrayList<>(player.board);
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public boolean addToHand(Card... cards) {
        verifyCards(cards);
        return hand.addAll(List.of(cards));
    }

    public boolean removeFromHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        return hand.remove(card);
    }

    public Card removeFromHand(int index) {
        return hand.remove(index);
    }

    public List<Card> removeFromHand() {
        List<Card> removedCards = new ArrayList<>(hand);
        hand.clear();
        return removedCards;
    }

    public List<Card> getBoard() {
        return Collections.unmodifiableList(board);
    }

    public boolean addToBoard(Card... cards) {
        verifyCards(cards);
        return board.addAll(List.of(cards));
    }

    private void verifyCards(Card[] cards) {
        if (cards == null) {
            throw new IllegalArgumentException("Cards cannot be null");
        }
        if (cards.length == 0) {
            throw new IllegalArgumentException("At least one card must be provided");
        }
        for (Card card : cards) {
            if (card == null) {
                throw new IllegalArgumentException("Cards cannot contain null values");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player player)) return false;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "Player{"
                + "name='"
                + name
                + "', hand="
                + Arrays.toString(hand.toArray(Card[]::new))
                + ", board="
                + Arrays.toString(board.toArray(Card[]::new))
                + '}';
    }
}
