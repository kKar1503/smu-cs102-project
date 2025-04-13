package parade.menu.display;

class CentralisedDisplay extends HorizontallyCentralisedDisplay {
    CentralisedDisplay(String... text) {
        super((terminalHeight - text.length) / 2, 0, text);
    }

    CentralisedDisplay(int topOffset, int leftOffset, String... text) {
        super((terminalHeight - text.length) / 2 + topOffset, leftOffset, text);
    }
}
