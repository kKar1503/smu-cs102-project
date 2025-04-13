package parade.menu.display;

import parade.menu.base.MenuResource;
import parade.menu.base.MenuResource.MenuResourceType;
import parade.utils.Ansi;

public class BasicWelcomeDisplay extends CentralisedDisplay {
    public BasicWelcomeDisplay() {
        super(MenuResource.getArray(MenuResourceType.BASIC_WELCOME_SCREEN));
    }

    @Override
    public void display() {
        clear();
        print(Ansi.PURPLE_BOLD.toString());
        super.display();
        print(Ansi.RESET.toString());
    }
}
