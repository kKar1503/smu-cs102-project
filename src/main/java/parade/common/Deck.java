package parade.common;

import java.util.Collections;
import java.util.LinkedList;

public class Deck {

    private final LinkedList<Card> cards;

    public Deck() {

        cards = new LinkedList<>();

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

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return (cards.removeLast());
        }
        return null;
    }
}
