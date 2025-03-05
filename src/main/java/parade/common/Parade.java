package parade.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parade {
    List<Card> paradeList = new LinkedList<>();

    // Constructor
    public Parade(List<Card> cardList) {
        // check that it is exactly 6 cards
        if (cardList.size() != 6) {
            throw new IllegalArgumentException("Give me 6 cards");
        }

        for (Card c : cardList) {
            this.paradeList.add(c);
        }
    }

    // PlaceCard
    public List<Card> placeCard(Card placeCard) {

        // List to store cards to be removed
        List<Card> removedCards = new ArrayList<>();

        // Remove mode
        if ((this.paradeList).size() > placeCard.getNumber()) {

            int removeZoneCardIndex = this.paradeList.size() - placeCard.getNumber();
            // Count from index of numbers
            for (int i = 0; i < removeZoneCardIndex; i++) { // i here is the index

                // Obtains card to compare
                Card cardAtIndex = paradeList.get(i);

                // Check which ones to remove (equal or less than)
                if (cardAtIndex.getNumber() <= placeCard.getNumber()
                        || cardAtIndex.getColour() == placeCard.getColour()) {
                    removedCards.add(cardAtIndex);
                }
            }

            // Remove from the cards
            paradeList.removeAll(removedCards);

            // Add placeCard
            paradeList.add(placeCard);
        }

        return removedCards;
    }
}
