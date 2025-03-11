package parade.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parade {
    List<Card> cards = new LinkedList<>();

    // Constructor
    public Parade(List<Card> cards) {
        // check that it is exactly 6 cards
        if (cards.size() != 6) {
            throw new IllegalArgumentException("Give me 6 cards");
        }

        this.cards.addAll(cards);
    }

    // PlaceCard
    public List<Card> placeCard(Card placeCard) {

        // List to store cards to be removed
        List<Card> removedCards = new ArrayList<>();

        // Remove mode
        if ((this.cards).size() > placeCard.getNumber()) {

            int removeZoneCardIndex = this.cards.size() - placeCard.getNumber();
            // Count from index of numbers
            for (int i = 0; i < removeZoneCardIndex; i++) { // i here is the index

                // Obtains card to compare
                Card cardAtIndex = cards.get(i);

                // Check which ones to remove (equal or less than)
                if (cardAtIndex.getNumber() <= placeCard.getNumber()
                        || cardAtIndex.getColour() == placeCard.getColour()) {
                    removedCards.add(cardAtIndex);
                }
            }

            // Remove from the cards
            cards.removeAll(removedCards);

            // Add placeCard
            cards.add(placeCard);
        }

        return removedCards;
    }

    public List<Card> getParadeCards() {
        return paradeList;
    }
}
