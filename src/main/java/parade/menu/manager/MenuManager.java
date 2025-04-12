package parade.menu.manager;

import parade.card.Card;
import parade.menu.option.LobbyMenuOption;
import parade.menu.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.ComputerController;
import parade.player.controller.PlayCardData;

import java.util.List;
import java.util.Map;

public interface MenuManager {
    void welcomeDisplay();

    MainMenuOption mainMenu();

    LobbyMenuOption lobbyMenu(List<Player> lobby);

    ComputerController newComputerMenu();

    void renderPlayerTurn(
            Player player, Card newlyDrawnCard, PlayCardData playCardData, boolean toDiscard);

    void renderEndGame(Map<Player, Integer> playerScores);

    void renderBye();
}
