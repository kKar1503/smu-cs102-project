package parade.menu.display;

import parade.card.Card;
import parade.card.Colour;
import parade.utils.Ansi;

import java.util.ArrayList;
import java.util.List;

public class HorizontalCardsDisplay extends AbstractCardsDisplay {

    private final List<Card> cards;
    private final boolean showIndices;

    public HorizontalCardsDisplay(List<Card> cards, boolean showIndices) {
        this.cards = cards;
        this.showIndices = showIndices;
    }

    @Override
    public void display() {
        String index;

        // StringBuilders for different card parts
        StringBuilder sbIndices = new StringBuilder();
        StringBuilder sbTop = new StringBuilder();
        StringBuilder sbMiddle = new StringBuilder();
        StringBuilder sbBottom = new StringBuilder();
        StringBuilder sbEmptyBox = new StringBuilder();

        if (cards == null || cards.isEmpty()) {
            // if no board, should return error!
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

        // Sort cards consistently by color and number
        List<Card> sortedBoard = new ArrayList<>(cards);

        // Render top border of the box
        println(
                TOP_LEFT
                        + HORIZONTAL_DOUBLE.repeat(sortedBoard.size() * TOTAL_CARD_WIDTH + PADDING)
                        + TOP_RIGHT);

        // Render index labels if requested
        if (showIndices) {
            sbIndices.append(VERTICAL_DOUBLE);
            for (int i = 0; i < sortedBoard.size(); i++) {
                index = String.format("[%d]", i + 1);
                int rightIndexPadding = TOTAL_CARD_WIDTH - index.length() - LEFT_INDEX_PADDING;

                sbIndices
                        .append(SPACE.repeat(LEFT_INDEX_PADDING))
                        .append(
                                printConsoleColour(
                                        sortedBoard.get(i).getColour().toString().toLowerCase(),
                                        index))
                        .append(SPACE.repeat(rightIndexPadding));
            }
            // Close the index row with a box edge
            sbIndices.append(SPACE.repeat(PADDING) + VERTICAL_DOUBLE);
            println(sbIndices.toString());
        }

        // Build each card row line by line
        sbTop.append(VERTICAL_DOUBLE);
        sbMiddle.append(VERTICAL_DOUBLE);
        sbBottom.append(VERTICAL_DOUBLE);

        for (Card card : sortedBoard) {
            sbTop.append(renderTopHalfCard(card, PADDING)).append(SPACE);
            sbMiddle.append(renderMiddleCard(card, PADDING)).append(SPACE);
            sbBottom.append(renderBottomHalfCard(card, PADDING)).append(SPACE);
        }

        // Close the content rows
        sbTop.append(SPACE.repeat(PADDING) + VERTICAL_DOUBLE);
        sbMiddle.append(SPACE.repeat(PADDING) + VERTICAL_DOUBLE);
        sbBottom.append(SPACE.repeat(PADDING) + VERTICAL_DOUBLE);

        // Print all rows
        println(sbTop.toString());
        println(sbMiddle.toString());
        println(sbBottom.toString());

        // Render bottom border of the box
        println(
                BOTTOM_LEFT
                        + HORIZONTAL_DOUBLE.repeat(sortedBoard.size() * TOTAL_CARD_WIDTH + PADDING)
                        + BOTTOM_RIGHT);
        flush();
    }
}
