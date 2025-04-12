package parade.menu.manager;

import parade.computer.ComputerEngine;
import parade.menu.menu.*;
import parade.menu.option.LobbyMenuOption;
import parade.menu.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;

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
    public String computerNameMenu() {
        return new ComputerNameMenu().start();
    }

    @Override
    public ComputerEngine computerDifficultyMenu() {
        return new ComputerDifficultyMenu().start();
    }

    @Override
    public AbstractPlayerController removePlayerMenu(List<AbstractPlayerController> controllers) {
        return new RemovePlayerMenu(controllers).start();
    }

    @Override
    public String humanNameMenu() {
        return new HumanNameMenu().start();
    }

    @Override
    public void renderRoll(int diceRoll1, int diceRoll2, List<Player> players) {
        String playerName = players.get((diceRoll1 + diceRoll2) % players.size()).getName();
        System.out.printf(
                "Dice roll: %d, %s will be starting first!%n", diceRoll1 + diceRoll2, playerName);
    }

    @Override
    public void renderBye() {
        System.out.println("Bye bye buddy.");
    }
}
