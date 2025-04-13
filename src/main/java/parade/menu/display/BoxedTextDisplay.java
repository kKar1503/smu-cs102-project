package parade.menu.display;

import parade.menu.base.AbstractDisplay;

import java.util.Arrays;

public class BoxedTextDisplay extends AbstractDisplay {
    private static final String HORIZONTAL = "─";
    private static final String VERTICAL = "│";
    private static final String TOP_LEFT = "┌";
    private static final String TOP_RIGHT = "┐";
    private static final String BOTTOM_LEFT = "└";
    private static final String BOTTOM_RIGHT = "┘";

    private final String[] text;
    private final int xPadding;
    private final int yPadding;
    private final int textWidth;
    private final int topOffset;
    private final int leftOffset;

    public BoxedTextDisplay(String text, int padding, boolean centralised) {
        this.text = text.split(NEW_LINE);
        this.xPadding = padding;
        this.yPadding = padding / 2;
        this.textWidth = Arrays.stream(this.text).mapToInt(String::length).max().orElse(0);
        this.topOffset =
                centralised ? (terminalHeight - this.text.length - this.yPadding * 2 - 2) / 2 : 0;
        this.leftOffset = centralised ? (terminalWidth - textWidth - this.xPadding * 2 - 2) / 2 : 0;
    }

    public BoxedTextDisplay(String text, int padding) {
        this(text, padding, true);
    }

    public BoxedTextDisplay(String text) {
        this(text, 1, true);
    }

    @Override
    public void display() {
        for (int i = 0; i < topOffset; i++) {
            println();
        }

        println(generateTopBorder());

        for (int i = 0; i < yPadding; i++) {
            println(generateVerticalPaddingRows());
        }

        for (String line : text) {
            print(generateLeftPadding());
            println(VERTICAL + " ".repeat(xPadding) + line + " ".repeat(xPadding) + VERTICAL);
        }

        for (int i = 0; i < yPadding; i++) {
            println(generateVerticalPaddingRows());
        }

        println(generateBottomBorder());
        flush();
    }

    private String generateLeftPadding() {
        return " ".repeat(leftOffset);
    }

    private String generateTopBorder() {
        return generateLeftPadding()
                + TOP_LEFT
                + HORIZONTAL.repeat(textWidth + xPadding * 2)
                + TOP_RIGHT;
    }

    private String generateBottomBorder() {
        return generateLeftPadding()
                + BOTTOM_LEFT
                + HORIZONTAL.repeat(textWidth + xPadding * 2)
                + BOTTOM_RIGHT;
    }

    private String generateVerticalPaddingRows() {
        return generateLeftPadding() + VERTICAL + " ".repeat(textWidth + xPadding * 2) + VERTICAL;
    }
}
