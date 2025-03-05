package parade.common;

import java.util.Collections;
import java.util.LinkedList;

public class Deck {

    private LinkedList<Card> cards;

    public Deck() {

        cards = new LinkedList<Card>();

        for (Colour colour : Colour.values()) {
            for (int i = 0; i <= 10; i++) {
                cards.add(new Card(i, colour));
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public boolean isDeckEmpty() {
        return cards.isEmpty();
    }

    public int cardsLeft() {
        return cards.size();
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return (cards.removeLast());
        }
        return null;
    }
}
