package parade.menu.menu;

import parade.computer.*;
import parade.menu.base.AbstractMenu;
import parade.menu.prompt.OptionsPrompt;

public class ComputerDifficultyMenu extends AbstractMenu<ComputerEngine> {
    private final OptionsPrompt prompt = new OptionsPrompt(true, "Easy", "Hard");

    @Override
    public ComputerEngine start() {
        printlnFlush("Choose computer player's difficulty");
        int userInput = prompt.prompt();
        return switch (userInput) {
            case 0 -> new EasyComputerEngine();
            case 1 -> new HardComputerEngine();
            default -> throw new IllegalStateException("Unexpected value: " + userInput);
        };
    }
}
