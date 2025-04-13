package parade.card;

import java.util.*;

public class Parade {
    List<Card> cards = new LinkedList<>();

    public Parade(List<Card> cards) {
        if (cards.size() != 6) {
            throw new IllegalArgumentException("Give me 6 cards");
        }

        this.cards.addAll(cards);
    }

    // Copy constructor
    public Parade(Parade parade) {
        this.cards = new LinkedList<>(parade.cards);
    }

    public List<Card> placeCard(Card placeCard) {
        List<Card> removedCards = new ArrayList<>();
        if ((this.cards).size() > placeCard.getNumber()) {

            int removeZoneCardIndex = this.cards.size() - placeCard.getNumber();
            for (int i = 0; i < removeZoneCardIndex; i++) { // i here is the index
                Card cardAtIndex = cards.get(i);
                if (cardAtIndex.getNumber() <= placeCard.getNumber()
                        || cardAtIndex.getColour() == placeCard.getColour()) {
                    removedCards.add(cardAtIndex);
                }
            }

            cards.removeAll(removedCards);
        }
        cards.add(placeCard);
        return removedCards;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    @Override
    public String toString() {
        return "Parade{" + "cards=" + Arrays.toString(cards.toArray(Card[]::new)) + '}';
    }
}
