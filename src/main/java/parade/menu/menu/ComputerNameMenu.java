package parade.menu.menu;

import parade.menu.base.AbstractMenu;
import parade.menu.prompt.StringPrompt;

public class ComputerNameMenu extends AbstractMenu<String> {
    @Override
    public String start() {
        printlnFlush("Enter computer's name: ");
        return new StringPrompt(true, true).prompt();
    }
}
