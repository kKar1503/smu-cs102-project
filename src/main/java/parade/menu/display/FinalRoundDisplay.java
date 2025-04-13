package parade.menu.display;

import parade.menu.base.MenuResource;

public class FinalRoundDisplay extends AbstractFullScreenBlinkingDisplay {
    public FinalRoundDisplay() {
        super(MenuResource.getArray(MenuResource.MenuResourceType.FINAL));
    }

    @Override
    public void display() {
        super.display();
        println();
        new HorizontallyCentralisedDisplay("No more drawing of cards this round.").display();
        println();
        flush();
        sleep(DEFAULT_FRAME_DELAY_MS, true);
    }
}
