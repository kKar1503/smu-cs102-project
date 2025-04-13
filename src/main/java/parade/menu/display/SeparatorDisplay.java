package parade.menu.display;

import parade.menu.base.AbstractDisplay;

import java.util.function.Function;

public class SeparatorDisplay extends AbstractDisplay {
    private static final String SEPARATOR = "=";

    private final String separator;

    public SeparatorDisplay() {
        this(null, null);
    }

    public SeparatorDisplay(Function<String, String> apply) {
        this(null, apply);
    }

    public SeparatorDisplay(String text) {
        this(text, null);
    }

    public SeparatorDisplay(String text, Function<String, String> apply) {
        int textSize = text != null ? text.length() + 2 : 0;
        int leftSeparatorSize = (terminalWidth - textSize) / 2;
        int rightSeparatorSize = terminalWidth - textSize - leftSeparatorSize;

        String separator = SEPARATOR.repeat(leftSeparatorSize);
        if (text != null) {
            separator += " " + text + " ";
        }
        separator += SEPARATOR.repeat(rightSeparatorSize);
        if (apply != null) {
            separator = apply.apply(separator);
        }
        this.separator = separator;
    }

    @Override
    public void display() {
        printlnFlush(separator);
    }

    @Override
    public String toString() {
        return separator;
    }
}
