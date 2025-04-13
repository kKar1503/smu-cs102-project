package parade.menu.display;

import parade.utils.Ansi;

class AbstractFullScreenBlinkingDisplay extends CentralisedDisplay {
    AbstractFullScreenBlinkingDisplay(String... text) {
        super(text);
    }

    @Override
    public void display() {
        print(Ansi.PURPLE.toString());
        for (int i = 0; i < 10; i++) {
            clear();
            sleep(100);
            super.display();
            sleep(100);
        }

        for (int i = 0; i < 5; i++) {
            clear();
            sleep(50);
            super.display();
            sleep(50);
        }
        sleep(500);
        printFlush(Ansi.RESET.toString());
    }
}
