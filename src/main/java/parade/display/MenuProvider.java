package parade.display;

import parade.card.Card;
import parade.display.option.LobbyMenuOption;
import parade.display.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.PlayCardData;

import java.util.List;
import java.util.Map;

public interface MenuProvider {
    void renderWelcome();

    MainMenuOption mainMenuPrompt();

    LobbyMenuOption renderPlayersLobby(List<Player> lobby);

    void renderComputerDifficulty();

    void renderPlayerTurn(
            Player player, Card newlyDrawnCard, PlayCardData playCardData, boolean toDiscard);

    void renderEndGame(Map<Player, Integer> playerScores);

    void renderBye();
}
