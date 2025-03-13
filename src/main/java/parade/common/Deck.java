package parade.common;

import parade.common.exceptions.EmptyDeckException;
import parade.common.exceptions.InsufficientCardException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck extends Stack<Card>{

    public Deck() {
        this.ensureCapacity(66); // We know it'll always be 6 (color) * 11 (0-10) cards
        for (Colour colour : Colour.values()) {
            for (int i = 0; i <= 10; i++) {
                this.push(new Card(i, colour));
            }
        }
        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(this);
    }

    /**
     * Removes the top card from the deck and returns it.
     *
     * @return The top card from the deck.
     * @throws EmptyDeckException if the deck is empty.
     */
    @Override
    public Card pop() throws EmptyDeckException {
        if (this.isEmpty()) throw new EmptyDeckException();
        return super.pop();
    }

    /**
     * Removes the top n card from the deck and returns them in a list.
     *
     * @param n number of cards to draw
     * @return A list of drawn cards.
     * @throws EmptyDeckException if the deck is empty.
     * @throws InsufficientCardException if there are not enough cards in the deck.
     */
    public List<Card> pop(int n) throws EmptyDeckException, InsufficientCardException {
        if (this.isEmpty()) {
            throw new EmptyDeckException();
        }
        if (this.size() < n) {
            throw new InsufficientCardException();
        }
        List<Card> drawnCards = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            drawnCards.add(this.pop());
        }
        return drawnCards;
    }
}
