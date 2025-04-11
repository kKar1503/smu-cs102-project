package parade.display;

import parade.display.option.MainMenuOption;

public class MainMenu extends AbstractNumericPrompt<MainMenuOption> {
    public MainMenu() {
        super(new String[] {"Start Game", "Exit"});
    }

    @Override
    public MainMenuOption prompt() {
        int userInput = promptForInput();
        return switch (userInput) {
            case 0 -> MainMenuOption.START_GAME;
            case 1 -> MainMenuOption.EXIT;
            default -> throw new IllegalStateException("Unexpected value: " + userInput);
        };
    }
}
