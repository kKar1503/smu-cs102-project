package parade.menu.menu;

import parade.exceptions.MenuCancelledException;
import parade.menu.base.AbstractMenu;
import parade.menu.prompt.StringPrompt;

public class HumanNameMenu extends AbstractMenu<String> {
    @Override
    public String start() throws MenuCancelledException {
        printFlush("Enter player name: ");
        return new StringPrompt().prompt();
    }
}
