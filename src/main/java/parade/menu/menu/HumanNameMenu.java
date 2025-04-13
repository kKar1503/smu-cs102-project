package parade.menu.menu;

import parade.exception.MenuCancelledException;
import parade.menu.base.AbstractMenu;
import parade.menu.prompt.StringPrompt;

public class HumanNameMenu extends AbstractMenu<String> {
    @Override
    public String start() throws MenuCancelledException {
        printlnFlush("Enter player name: ");
        return new StringPrompt(true, true).prompt();
    }
}
