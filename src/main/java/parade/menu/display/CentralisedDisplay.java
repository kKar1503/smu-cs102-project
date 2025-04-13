package parade.menu.display;

import parade.menu.base.AbstractDisplay;

import java.util.Arrays;

class CentralisedDisplay extends AbstractDisplay {
    private final String[] text;
    private final int topOffset;
    private final int leftOffset;

    CentralisedDisplay(String[] text) {
        this(text, 0, 0);
    }

    CentralisedDisplay(String[] text, int topOffset, int leftOffset) {
        this.text = text;

        this.topOffset = (terminalHeight - text.length) / 2 + topOffset;
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
