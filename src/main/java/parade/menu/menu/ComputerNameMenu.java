package parade.menu.menu;

import parade.menu.base.AbstractMenu;
import parade.menu.prompt.StringPrompt;

public class ComputerNameMenu extends AbstractMenu<String> {
    private final StringPrompt prompt = new StringPrompt();

    @Override
    public String start() {
        printFlush("Enter computer's name: ");
        return prompt.prompt();
    }
}
