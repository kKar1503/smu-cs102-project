package parade.menu.menu;

import parade.menu.base.AbstractMenu;
import parade.menu.base.MenuResource;
import parade.menu.display.DynamicSeparator;
import parade.menu.option.MainMenuOption;
import parade.menu.prompt.OptionsPrompt;
import parade.utils.Ansi;

public class AsciiMainMenu extends AbstractMenu<MainMenuOption> {
    private final OptionsPrompt prompt =
            new OptionsPrompt(
                    Ansi.PURPLE.apply(
                            MenuResource.get(MenuResource.MenuResourceType.BASIC_WELCOME_MENU)),
                    2,
                    false);

    @Override
    public MainMenuOption start() {
        new DynamicSeparator("Welcome to Parade!", Ansi.PURPLE_BOLD::apply).display();
        int userInput = prompt.prompt();
        return switch (userInput) {
            case 0 -> MainMenuOption.START_GAME;
            case 1 -> MainMenuOption.EXIT;
            default -> throw new IllegalStateException("Unexpected value: " + userInput);
        };
    }
}
