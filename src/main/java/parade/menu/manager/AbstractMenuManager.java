package parade.menu.manager;

import parade.menu.menu.*;
import parade.menu.option.LobbyMenuOption;
import parade.menu.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.ComputerController;

import java.util.List;

abstract class AbstractMenuManager implements MenuManager {
    @Override
    public MainMenuOption mainMenu() {
        return new MainMenu().start();
    }

    @Override
    public LobbyMenuOption lobbyMenu(List<Player> players) {
        return new LobbyMenu(players).start();
    }

    @Override
    public ComputerController newComputerMenu() {
        return new NewComputerMenu().start();
    }

    @Override
    public AbstractPlayerController removePlayerMenu(List<AbstractPlayerController> controllers) {
        return new RemovePlayerMenu(controllers).start();
    }
}
