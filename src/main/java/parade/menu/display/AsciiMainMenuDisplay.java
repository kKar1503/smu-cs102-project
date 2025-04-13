package parade.menu.display;

import parade.menu.base.MenuResource;
import parade.menu.base.MenuResource.MenuResourceType;
import parade.utils.Ansi;

public class AsciiMainMenuDisplay extends HorizontallyCentralisedDisplay {
    public AsciiMainMenuDisplay() {
        super(MenuResource.getArray(MenuResourceType.MAIN_MENU));
    }

    @Override
    public void display() {
        print(Ansi.PURPLE_BOLD.toString());
        super.display();
        print(Ansi.RESET.toString());
    }
}
