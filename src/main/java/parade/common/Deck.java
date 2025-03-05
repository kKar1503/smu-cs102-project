package parade.common;

import parade.textrenderer.DebugRendererProvider;

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
        DebugRendererProvider.getInstance().debug("Shuffling deck");
        Collections.shuffle(cards);
    }

    public boolean isDeckEmpty() {
        return cards.isEmpty();
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            Card cardDrawn = cards.removeLast();
            DebugRendererProvider.getInstance().debug("Drawn card: " + cardDrawn);
            return cardDrawn;
        }
        return null;
    }
}
