package parade.display;

import parade.display.option.MainMenuOption;
import parade.utils.Ansi;

public class AsciiWelcomeScreen extends AbstractNumericPrompt<MainMenuOption> {
    AsciiWelcomeScreen() {
        super(
                Ansi.PURPLE.apply(
                        MenuResource.get(MenuResource.MenuResourceType.ADVANCED_WELCOME_SCREEN)),
                2);
    }

    @Override
    public void display() {
        new DynamicSeparator("Welcome to Parade!", Ansi.PURPLE_BOLD::apply);
        super.display();
        new DynamicSeparator(Ansi.PURPLE_BOLD::apply);
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
