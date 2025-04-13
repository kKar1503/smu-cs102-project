package parade.menu.display;

import java.util.Arrays;

class CentralisedDisplay extends HorizontallyCentralisedDisplay {
    CentralisedDisplay(String[] text) {
        super(text, (terminalHeight - text.length) / 2, 0);
    }

    CentralisedDisplay(String[] text, int topOffset, int leftOffset) {
        super(text, (terminalHeight - text.length) / 2 + topOffset, leftOffset);
    }
}
