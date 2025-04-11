package parade.display;

import static parade.constants.GameEngineValues.*;

import parade.display.option.LobbyMenuOption;
import parade.player.Player;

import java.util.List;

public class LobbyMenu extends AbstractNumericPrompt<LobbyMenuOption> {
    private final List<Player> players;

    public LobbyMenu(List<Player> players) {
        super(
                new String[] {
                    "Add Player" + (players.size() == MAX_PLAYERS ? " (Lobby is full)" : ""),
                    "Add Computer" + (players.size() == MAX_PLAYERS ? " (Lobby is full)" : ""),
                    "Remove player/computer" + (players.isEmpty() ? " (Lobby is empty)" : ""),
                    "Start Game" + (players.size() < MIN_PLAYERS ? " (Not enough players)" : ""),
                });
        this.players = players;
    }

    @Override
    public void display() {
        println("Players in lobby: ");
        for (int i = 1; i <= players.size(); i++) {
            printf("%d. %s%n", i, players.get(i - 1).getName());
        }
        println();

        super.display();
    }

    @Override
    public LobbyMenuOption prompt() {
        int userInput = promptForInput();
        return switch (userInput) {
            case 0 -> LobbyMenuOption.ADD_PLAYER;
            case 1 -> LobbyMenuOption.ADD_COMPUTER;
            case 2 -> LobbyMenuOption.REMOVE_PLAYER;
            case 3 -> LobbyMenuOption.START_GAME;
            default -> throw new IllegalStateException("Unexpected value: " + userInput);
        };
    }
}
