package parade.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Player implements Serializable {
    @Serial private static final long serialVersionUID = 3559231237142260576L;

    private final UUID id = UUID.randomUUID();
    private final String name;
    private final List<Card> hand = new LinkedList<>();
    private final List<Card> board = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public boolean addToHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        return hand.add(card);
    }

    public boolean removeFromHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        return hand.remove(card);
    }

    public List<Card> getBoard() {
        return Collections.unmodifiableList(board);
    }

    public boolean addToBoard(Card... cards) {
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
        return board.addAll(List.of(cards));
    }

    @Override
    public String toString() {
        return "Player{"
                + "id='"
                + id
                + "', name='"
                + name
                + "', hand="
                + Arrays.toString(hand.toArray(Card[]::new))
                + ", board="
                + Arrays.toString(board.toArray(Card[]::new))
                + '}';
    }
}
