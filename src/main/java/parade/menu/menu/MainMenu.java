package parade.menu.menu;

import parade.menu.base.AbstractMenu;
import parade.menu.option.MainMenuOption;
import parade.menu.prompt.OptionsPrompt;

public class MainMenu extends AbstractMenu<MainMenuOption> {
    private final OptionsPrompt prompt = new OptionsPrompt("Start Game", "Exit");

    @Override
    public MainMenuOption start() {
        int userInput = prompt.prompt();
        return switch (userInput) {
            case 0 -> MainMenuOption.START_GAME;
            case 1 -> MainMenuOption.EXIT;
            default -> throw new IllegalStateException("Unexpected value: " + userInput);
        };
    }
}
