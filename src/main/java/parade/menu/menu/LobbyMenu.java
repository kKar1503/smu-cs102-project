package parade.menu.menu;

import static parade.constants.GameEngineValues.*;

import parade.menu.base.AbstractMenu;
import parade.menu.option.LobbyMenuOption;
import parade.menu.prompt.OptionsPrompt;
import parade.player.Player;

import java.util.List;

public class LobbyMenu extends AbstractMenu<LobbyMenuOption> {
    private final List<Player> players;
    private final OptionsPrompt prompt;

    public LobbyMenu(List<Player> players) {
        prompt =
                new OptionsPrompt(
                        "Add Player" + (players.size() == MAX_PLAYERS ? " (Lobby is full)" : ""),
                        "Add Computer" + (players.size() == MAX_PLAYERS ? " (Lobby is full)" : ""),
                        "Remove player/computer" + (players.isEmpty() ? " (Lobby is empty)" : ""),
                        "Start Game"
                                + (players.size() < MIN_PLAYERS ? " (Not enough players)" : ""));
        this.players = players;
    }

    @Override
    public LobbyMenuOption start() {
        while (true) {
            println("Players in lobby: ");
            for (int i = 1; i <= players.size(); i++) {
                printf("%d. %s%n", i, players.get(i - 1).getName());
            }
            println();
            int userInput = prompt.prompt();
            switch (userInput) {
                case 0:
                    if (players.size() == MAX_PLAYERS) {
                        printlnFlush("Lobby is full.");
                        continue;
                    }
                    return LobbyMenuOption.ADD_PLAYER;
                case 1:
                    if (players.size() == MAX_PLAYERS) {
                        printlnFlush("Lobby is full.");
                        continue;
                    }
                    return LobbyMenuOption.ADD_COMPUTER;
                case 2:
                    if (players.isEmpty()) {
                        printlnFlush("Lobby has no players.");
                        continue;
                    }
                    return LobbyMenuOption.REMOVE_PLAYER;
                case 3:
                    if (players.size() < MIN_PLAYERS) {
                        printlnFlush("Lobby does not have enough players.");
                        continue;
                    }
                    return LobbyMenuOption.START_GAME;
                default:
                    throw new IllegalStateException("Unexpected value: " + userInput);
            }
        }
    }
}
