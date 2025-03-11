package parade.common;

import java.util.*;

public class Deck {

    private final Stack<Card> cards;

    public Deck() {
        cards = new Stack<>();
        cards.ensureCapacity(66); // We know it'll always be 6 (color) * 11 (0-10) cards
        for (Colour colour : Colour.values()) {
            for (int i = 0; i <= 10; i++) {
                cards.add(new Card(i, colour));
            }
        }

        shuffleDeck();
    }

    private void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public boolean isDeckEmpty() {
        return cards.isEmpty();
    }

    public Card draw() {
        return cards.isEmpty() ? null : cards.pop();
    }

    public List<Card> draw(int count) {
        List<Card> drawnCards = new ArrayList<>(count);
        if (cards.isEmpty()) {
            return drawnCards;
        }
        for (int i = 0; i < count; i++) {
            Card drawnCard = cards.pop();
            drawnCards.add(drawnCard);
        }
        return drawnCards;
    }
}
