package parade.common;

public class Card {

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

    public String toString() {
        return "Card{number=" + number + ", colour=" + colour + "}";
    }
}
