package parade.display;

import parade.display.option.MainMenuOption;
import parade.utils.Ansi;

public class AsciiMainMenu extends AbstractNumericPrompt<MainMenuOption> {
    AsciiMainMenu() {
        super(
                Ansi.PURPLE.apply(
                        MenuResource.get(MenuResource.MenuResourceType.BASIC_WELCOME_MENU)),
                2);
    }

    @Override
    public void display() {
        new DynamicSeparator("Welcome to Parade!", Ansi.PURPLE_BOLD::apply);
        super.display();
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
