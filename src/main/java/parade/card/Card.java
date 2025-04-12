package parade.card;

/**
 * Represents a single card in the Parade game.
 * A card has a numeric value and a color, and supports natural ordering
 * for sorting (by color first, then by number).
 */
public class Card implements Comparable<Card> {
    private final int number;     // The numeric value of the card
    private final Colour colour;  // The color category of the card

    /**
     * Constructs a new card with the given number and color.
     *
     * @param number the numeric value of the card
     * @param colour the color of the card
     */
    public Card(int number, Colour colour) {
        this.number = number;
        this.colour = colour;
    }

    /**
     * Returns the numeric value of the card.
     *
     * @return the number of the card
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the color of the card.
     *
     * @return the colour enum of the card
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Returns a string representation of the card,
     * including both number and color.
     *
     * @return a string representing the card's details
     */
    @Override
    public String toString() {
        return "Card{number=" + number + ", colour=" + colour + "}";
    }

    /**
     * Compares this card with another card for natural ordering.
     * Cards are first ordered alphabetically by color name,
     * and then numerically by their number value.
     *
     * This allows cards to be sorted consistently using Collections.sort().
     *
     * @param other the other card to compare against
     * @return a negative integer, zero, or a positive integer as this card
     *         is less than, equal to, or greater than the specified card
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