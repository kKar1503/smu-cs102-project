package parade.menu.menu;

import parade.computer.ComputerEngine;
import parade.exceptions.MenuCancelledException;
import parade.menu.base.AbstractMenu;
import parade.player.controller.ComputerController;

public class NewComputerMenu extends AbstractMenu<ComputerController> {
    @Override
    public ComputerController start() throws MenuCancelledException {
        String computerName = new ComputerNameMenu().start();
        ComputerEngine computerEngine = new ComputerDifficultyMenu().start();
        return new ComputerController(computerName, computerEngine);
    }
}
