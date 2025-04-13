package parade.menu.manager;

import parade.computer.ComputerEngine;
import parade.menu.option.LobbyMenuOption;
import parade.menu.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.PlayCardData;

import java.util.List;
import java.util.Map;

public interface MenuManager {
    void welcomeDisplay();

    MainMenuOption mainMenu();

    LobbyMenuOption lobbyMenu(List<Player> lobby);

    void renderRoll(int diceRoll1, int diceRoll2, List<Player> players);

    String computerNameMenu();

    ComputerEngine computerDifficultyMenu();

    String humanNameMenu();

    AbstractPlayerController removePlayerMenu(List<AbstractPlayerController> controllers);

    int renderPlayerTurn(Player player, PlayCardData playCardData, boolean toDiscard);

    void finalRoundDisplay();

    void renderEndGame(Map<AbstractPlayerController, Integer> playerScores);

    void renderBye();
}
