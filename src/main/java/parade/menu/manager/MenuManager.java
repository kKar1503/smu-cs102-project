package parade.menu.manager;

import parade.card.Card;
import parade.computer.ComputerEngine;
import parade.core.result.GameResult;
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

    LobbyMenuOption lobbyMenu(List<Player> lobby, int minPlayers, int maxPlayers);

    void diceRollDisplay(int diceRoll1, int diceRoll2, List<Player> players);

    String computerNameMenu();

    ComputerEngine computerDifficultyMenu();

    String humanNameMenu();

    AbstractPlayerController removePlayerMenu(List<AbstractPlayerController> controllers);

    int playerTurnMenu(Player player, PlayCardData playCardData, boolean toDiscard);

    void playerMoveDisplay(Player player, Card playedCard, List<Card> cardsFromParade);

    void finalRoundDisplay();

    void endGameDisplay(Map<AbstractPlayerController, Integer> playerScores, GameResult result);

    void byeByeDisplay();
}
