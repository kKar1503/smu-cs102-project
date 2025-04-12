package parade.menu.display;

import parade.menu.base.AbstractDisplay;

import java.util.function.Function;

public class DynamicSeparator extends AbstractDisplay {
    private static final String SEPARATOR = "=";

    private final String separator;

    public DynamicSeparator() {
        this(null, null);
    }

    public DynamicSeparator(Function<String, String> apply) {
        this(null, apply);
    }

    public DynamicSeparator(String text) {
        this(text, null);
    }

    public DynamicSeparator(String text, Function<String, String> apply) {
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
