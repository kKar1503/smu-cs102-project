package parade.menu.display;

import parade.card.Card;
import parade.card.Colour;

import java.util.*;

public class StackedCardsDisplay extends AbstractCardsDisplay {
    private final List<Card> cards;

    public StackedCardsDisplay(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public void display() {
        // StringBuilders for different card parts
        StringBuilder sbEmptyBox = new StringBuilder();

        if (cards == null || cards.isEmpty()) {
            // Print an empty box layout if no cards are present

            sbEmptyBox.append(
                    TOP_LEFT
                            + HORIZONTAL_DOUBLE.repeat(PADDING)
                            + HORIZONTAL_DOUBLE.repeat(EMPTY_BOX_TEXT.length())
                            + HORIZONTAL_DOUBLE.repeat(PADDING)
                            + TOP_RIGHT
                            + NEW_LINE);
            sbEmptyBox.append(
                    VERTICAL_DOUBLE
                            + SPACE.repeat(PADDING)
                            + EMPTY_BOX_TEXT
                            + SPACE.repeat(PADDING)
                            + VERTICAL_DOUBLE
                            + NEW_LINE);

            sbEmptyBox.append(
                    BOTTOM_LEFT
                            + HORIZONTAL_DOUBLE.repeat(PADDING)
                            + HORIZONTAL_DOUBLE.repeat(EMPTY_BOX_TEXT.length())
                            + HORIZONTAL_DOUBLE.repeat(PADDING)
                            + BOTTOM_RIGHT
                            + NEW_LINE);

            printlnFlush(sbEmptyBox.toString());
            return;
        }

        // Group cards by colour
        Map<Colour, List<Card>> colourCardMap = new LinkedHashMap<>();
        for (Card card : cards) {
            colourCardMap.computeIfAbsent(card.getColour(), k -> new ArrayList<>()).add(card);
        }

        // Get height of tallest stack
        int maxHeight = colourCardMap.values().stream().mapToInt(List::size).max().orElse(0);
        int boxWidth = colourCardMap.size() * TOTAL_CARD_WIDTH;

        // Print top border
        println(TOP_LEFT + HORIZONTAL_DOUBLE.repeat(boxWidth + PADDING) + TOP_RIGHT);

        // Print each visual row of stacked cards
        for (int row = 0; row < maxHeight + 2; row++) {
            StringBuilder line = new StringBuilder(VERTICAL_DOUBLE);

            for (List<Card> cards : colourCardMap.values()) {
                int count = cards.size();
                String part;

                if (row < count) {
                    part = renderTopHalfCard(cards.get(row), PADDING);
                } else if (row == count) {
                    part = renderMiddleCard(cards.get(count - 1), PADDING);
                } else if (row == count + 1) {
                    part = renderBottomHalfCard(cards.get(count - 1), PADDING);
                } else {
                    part = SPACE.repeat(TOTAL_CARD_WIDTH - 1);
                }

                line.append(part);
            }

            // Close row with box edge
            line.append(SPACE.repeat(PADDING + colourCardMap.size()));
            line.append(VERTICAL_DOUBLE);
            println(line.toString());
        }

        // Print bottom border
        println(BOTTOM_LEFT + HORIZONTAL_DOUBLE.repeat(boxWidth + PADDING) + BOTTOM_RIGHT);
        flush();
    }
}
