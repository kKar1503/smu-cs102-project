package parade.menu.display;

import parade.menu.base.MenuResource;

public class FinalRoundDisplay extends AbstractFullScreenBlinkingDisplay {
    public FinalRoundDisplay() {
        super(MenuResource.getArray(MenuResource.MenuResourceType.ASCII_FINAL));
    }
}
