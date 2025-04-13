package parade.menu.display;

import parade.menu.base.*;
import parade.menu.base.MenuResource.MenuResourceType;
import parade.utils.Ansi;

public class AdvancedWelcomeDisplay extends HorizontallyCentralisedDisplay {
    public AdvancedWelcomeDisplay() {
        super(MenuResource.getArray(MenuResourceType.ADVANCED_WELCOME_SCREEN));
    }

    @Override
    public void display() {
        clear();
        print(Ansi.PURPLE_BOLD.toString());
        super.display();
        print(Ansi.RESET.toString());
    }
}
