package parade.menu.menu;

import parade.menu.base.AbstractMenu;
import parade.menu.option.LobbyMenuOption;
import parade.menu.prompt.OptionsPrompt;
import parade.player.Player;

import java.util.List;

public class LobbyMenu extends AbstractMenu<LobbyMenuOption> {
    private final List<Player> players;
    private final OptionsPrompt prompt;
    private final int minPlayers;
    private final int maxPlayers;

    public LobbyMenu(List<Player> players, int minPlayers, int maxPlayers) {
        prompt =
                new OptionsPrompt(
                        "Add Player" + (players.size() == maxPlayers ? " (Lobby is full)" : ""),
                        "Add Computer" + (players.size() == maxPlayers ? " (Lobby is full)" : ""),
                        "Remove player/computer" + (players.isEmpty() ? " (Lobby is empty)" : ""),
                        "Start Game" + (players.size() < minPlayers ? " (Not enough players)" : ""),
                        "Quit Game");
        this.players = players;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
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
                    if (players.size() == maxPlayers) {
                        printlnFlush("Lobby is full.");
                        continue;
                    }
                    return LobbyMenuOption.ADD_PLAYER;
                case 1:
                    if (players.size() == maxPlayers) {
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
                    if (players.size() < minPlayers) {
                        printlnFlush("Lobby does not have enough players.");
                        continue;
                    }
                    return LobbyMenuOption.START_GAME;
                case 4:
                    return LobbyMenuOption.QUIT_GAME;
                default:
                    throw new IllegalStateException("Unexpected value: " + userInput);
            }
        }
    }
}
