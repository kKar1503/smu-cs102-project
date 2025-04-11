package parade.display;

import java.util.function.Function;

public class DynamicSeparator extends AbstractDynamicDisplay {
    private static final String SEPARATOR = "=";

    private final String text;
    private final Function<String, String> apply;

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
        this.text = text;
        this.apply = apply;
    }

    @Override
    public void display() {
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
        printlnFlush(separator);
    }
}
