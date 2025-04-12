package parade.menu.manager;

import parade.card.Card;
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

    String computerNameMenu();

    ComputerEngine computerDifficultyMenu();

    String humanNameMenu();

    AbstractPlayerController removePlayerMenu(List<AbstractPlayerController> controllers);

    void renderPlayerTurn(
            Player player, Card newlyDrawnCard, PlayCardData playCardData, boolean toDiscard);

    void renderEndGame(Map<AbstractPlayerController, Integer> playerScores);

    void renderBye();

    void renderRoll(int diceRoll1, int diceRoll2, List<Player> players);
}
