package parade.card;

public class Card implements Comparable<Card> {
    private final int number;
    private final Colour colour;

    public Card(int number, Colour colour) {
        this.number = number;
        this.colour = colour;
    }

    public int getNumber() {
        return number;
    }

    public Colour getColour() {
        return colour;
    }

    @Override
    public String toString() {
        return "Card{number=" + number + ", colour=" + colour + "}";
    }

    /**
     * Compares this card with another card for natural ordering. Cards are first ordered
     * alphabetically by color name, and then numerically by their number value.
     *
     * <p>This allows cards to be sorted consistently using Collections.sort().
     *
     * @param other the other card to compare against
     * @return a negative integer, zero, or a positive integer as this card is less than, equal to,
     *     or greater than the specified card
     */
    @Override
    public int compareTo(Card other) {
        int colorCompare = this.colour.name().compareTo(other.colour.name());
        if (colorCompare != 0) {
            return colorCompare;
        }
        return Integer.compare(this.number, other.number);
    }
}
