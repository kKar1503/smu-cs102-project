package parade.menu.display;

import parade.menu.base.AbstractDisplay;

import java.util.Arrays;

class HorizontallyCentralisedDisplay extends AbstractDisplay {
    private final String[] text;
    private final int topOffset;
    private final int leftOffset;

    HorizontallyCentralisedDisplay(String[] text) {
        this(text, 0, 0);
    }

    HorizontallyCentralisedDisplay(String[] text, int topOffset, int leftOffset) {
        this.text = text;

        this.topOffset = topOffset;
        int maxLength = Arrays.stream(text).mapToInt(String::length).max().orElse(0);
        this.leftOffset = (terminalWidth - maxLength) / 2 + leftOffset;
    }

    @Override
    public void display() {
        for (int i = 0; i < topOffset; i++) {
            println();
        }

        String offset = " ".repeat(leftOffset);

        for (String line : text) {
            println(offset + line);
        }
        flush();
    }
}
