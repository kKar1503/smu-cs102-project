package parade.common;

import parade.textrenderer.DebugRendererProvider;

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
        DebugRendererProvider.getInstance().debug("Shuffling deck");
        Collections.shuffle(cards);
    }

    public boolean isDeckEmpty() {
        return cards.isEmpty();
    }

    public Card draw() {
        if (cards.isEmpty()) {
            DebugRendererProvider.getInstance().debug("Attempted to draw from an empty deck");
            return null;
        }
        Card drawnCard = cards.pop();
        DebugRendererProvider.getInstance().debug("Drawn card: " + drawnCard);
        return drawnCard;
    }

    public List<Card> draw(int count) {
        List<Card> drawnCards = new ArrayList<>(count);
        if (cards.isEmpty()) {
            DebugRendererProvider.getInstance().debug("Attempted to draw from an empty deck");
            return drawnCards;
        }
        for (int i = 0; i < count; i++) {
            Card drawnCard = cards.pop();
            DebugRendererProvider.getInstance().debug("Drawn card: " + drawnCard);
            drawnCards.add(drawnCard);
        }
        return drawnCards;
    }
}
