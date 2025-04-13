package parade.menu.display;

import parade.card.Card;
import parade.card.Colour;
import parade.menu.base.AbstractDisplay;
import parade.utils.Ansi;

abstract class AbstractCardsDisplay extends AbstractDisplay {
    static final int PADDING = 3;
    static final int WIDTH = 5; // card width
    static final int LEFT_INDEX_PADDING = (PADDING + WIDTH / 2) - 1;
    static final int TOTAL_CARD_WIDTH = PADDING + WIDTH + 2;

    static final String TOP_LEFT = "╔";
    static final String TOP_RIGHT = "╗";
    static final String BOTTOM_LEFT = "╚";
    static final String BOTTOM_RIGHT = "╝";
    static final String HORIZONTAL_DOUBLE = "═";
    static final String VERTICAL_DOUBLE = "║";
    static final String EMPTY_BOX_TEXT = "No cards to display.";
    static final String SPACE = " ";

    static final String CURVE_TOP_LEFT = "╭";
    static final String CURVE_TOP_RIGHT = "╮";
    static final String CURVE_BOTTOM_LEFT = "╰";
    static final String CURVE_BOTTOM_RIGHT = "╯";
    static final String HORIZONTAL = "─";
    static final String VERTICAL = "│";

    String printConsoleColour(String colour, String colourisedString) {
        return switch (colour) {
            case "red" -> Ansi.RED.apply(colourisedString);
            case "black" -> Ansi.GREY.apply(colourisedString);
            case "green" -> Ansi.GREEN.apply(colourisedString);
            case "blue" -> Ansi.BLUE.apply(colourisedString);
            case "yellow" -> Ansi.YELLOW.apply(colourisedString);
            case "purple" -> Ansi.PURPLE.apply(colourisedString);
            default -> colourisedString; // no colour - white
        };
    }

    String renderTopHalfCard(Card card, int gaps) {
        String num = String.valueOf(card.getNumber());

        String top =
                SPACE.repeat(gaps)
                        + CURVE_TOP_LEFT
                        + num
                        + HORIZONTAL.repeat(5 - 1 - num.length())
                        + CURVE_TOP_RIGHT;

        return printConsoleColour(card.getColour().toString().toLowerCase(), top);
    }

    String renderMiddleCard(Card card, int gaps) {
        String emoji =
                switch (card.getColour()) {
                    case Colour.BLACK -> "🐇"; // rabbit
                    case Colour.BLUE -> "👧"; // alice
                    case Colour.GREEN -> "🥚"; // egg
                    case Colour.RED -> "🎩"; // mad hatter
                    case Colour.YELLOW -> "🦆"; // dodo -> duck
                    case Colour.PURPLE -> "🐈"; // cat
                };

        String middle = SPACE.repeat(gaps) + VERTICAL + SPACE + emoji + SPACE + VERTICAL;

        return printConsoleColour(card.getColour().toString().toLowerCase(), middle);
    }

    String renderBottomHalfCard(Card card, int gaps) {
        String bottom =
                SPACE.repeat(gaps)
                        + CURVE_BOTTOM_LEFT
                        + HORIZONTAL.repeat(5 - 1)
                        + CURVE_BOTTOM_RIGHT;
        return printConsoleColour(card.getColour().toString().toLowerCase(), bottom);
    }

    /**
     * Renders a full single card (3 rows: top, middle, bottom) as a string. This is mostly used
     * when displaying a standalone card (e.g., drawn card).
     *
     * @param card The card to render.
     * @param gaps The number of spaces to pad on the left and right of the card's content.
     * @return A full multiline string representing the card.
     */
    String renderSingleCard(Card card, int gaps) {
        return System.lineSeparator()
                + renderTopHalfCard(card, gaps)
                + System.lineSeparator()
                + renderMiddleCard(card, gaps)
                + System.lineSeparator()
                + renderBottomHalfCard(card, gaps);
    }
}
