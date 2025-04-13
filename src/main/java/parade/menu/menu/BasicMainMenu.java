package parade.menu.menu;

import parade.menu.base.AbstractMenu;
import parade.menu.display.AsciiMainMenuDisplay;
import parade.menu.option.MainMenuOption;
import parade.menu.prompt.NumericPrompt;

public class BasicMainMenu extends AbstractMenu<MainMenuOption> {
    private final NumericPrompt prompt = new NumericPrompt(2, false);

    @Override
    public MainMenuOption start() {
        new AsciiMainMenuDisplay().display();
        moveCursor(terminalHeight, 0);
        int userInput = prompt.prompt();
        return switch (userInput) {
            case 0 -> MainMenuOption.START_GAME;
            case 1 -> MainMenuOption.EXIT;
            default -> throw new IllegalStateException("Unexpected value: " + userInput);
        };
    }
}
