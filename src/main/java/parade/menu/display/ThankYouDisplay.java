package parade.menu.display;

import parade.menu.base.MenuResource;

public class ThankYouDisplay extends AbstractFullScreenBlinkingDisplay {
    public ThankYouDisplay() {
        super(MenuResource.getArray(MenuResource.MenuResourceType.THANK_YOU));
    }
}
