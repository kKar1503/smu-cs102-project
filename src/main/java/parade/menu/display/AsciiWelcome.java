package parade.menu.display;

import parade.menu.base.*;
import parade.menu.base.MenuResource.MenuResourceType;
import parade.utils.Ansi;

public class AsciiWelcome extends AbstractDisplay {
    @Override
    public void display() {
        new DynamicSeparator("Welcome to Parade!", Ansi.PURPLE_BOLD::apply).display();
        printlnFlush(Ansi.PURPLE.apply(MenuResource.get(MenuResourceType.ADVANCED_WELCOME_SCREEN)));
        new DynamicSeparator(Ansi.PURPLE_BOLD::apply).display();
    }
}
