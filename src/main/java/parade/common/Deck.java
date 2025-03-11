package parade.common;

import parade.common.exceptions.EmptyDeckException;
import parade.common.exceptions.InsufficientCardException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

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

        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(cards);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    /**
     * Removes the top card from the deck and returns it.
     *
     * @return The top card from the deck.
     * @throws EmptyDeckException if the deck is empty.
     */
    public Card draw() throws EmptyDeckException {
        if (cards.isEmpty()) throw new EmptyDeckException();
        return cards.pop();
    }

    /**
     * Removes the top n card from the deck and returns them in a list.
     *
     * @param n number of cards to draw
     * @return A list of drawn cards.
     * @throws EmptyDeckException if the deck is empty.
     * @throws InsufficientCardException if there are not enough cards in the deck.
     */
    public List<Card> draw(int n) throws EmptyDeckException, InsufficientCardException {
        if (cards.isEmpty()) {
            throw new EmptyDeckException();
        }
        if (cards.size() < n) {
            throw new InsufficientCardException();
        }
        List<Card> drawnCards = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Card drawnCard = cards.pop();
            drawnCards.add(drawnCard);
        }
        return drawnCards;
    }
}
