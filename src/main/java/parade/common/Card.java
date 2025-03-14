package parade.common;

import java.io.Serial;
import java.io.Serializable;

public class Card implements Serializable {
    @Serial private static final long serialVersionUID = -2719634888825237988L;

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
