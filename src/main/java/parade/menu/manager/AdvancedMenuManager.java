package parade.menu.manager;

import parade.core.result.GameResult;
import parade.menu.display.AdvancedWelcomeDisplay;
import parade.menu.display.BoxedTextDisplay;
import parade.menu.display.DiceDisplay;
import parade.menu.display.EndGameScoreBoardDisplay;
import parade.menu.display.GameOverDisplay;
import parade.menu.display.ThankYouDisplay;
import parade.menu.display.WinnerResultDisplay;
import parade.menu.menu.AdvancedPlayerTurnMenu;
import parade.menu.menu.BasicMainMenu;
import parade.menu.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.PlayCardData;

import java.util.List;
import java.util.Map;

/**
 * AdvancedClientRenderer provides advanced rendering capabilities for the Parade game. It outputs
 * styled game content to the console.
 */
public class AdvancedMenuManager extends AbstractMenuManager {
    @Override
    public void welcomeDisplay() throws IllegalStateException {
        new AdvancedWelcomeDisplay().display();
    }

    @Override
    public MainMenuOption mainMenu() {
        return new BasicMainMenu().start();
    }

    @Override
    public void diceRollDisplay(int diceRoll1, int diceRoll2, List<Player> players) {
        clear();
        new DiceDisplay(diceRoll1, diceRoll2).display();
        sleep();
        clear();
        new BoxedTextDisplay(
                        String.format(
                                "%s will play first!",
                                getChosenPlayerFromDice(diceRoll1 + diceRoll2, players)),
                        2,
                        true)
                .display();
        sleep();
    }

    @Override
    public int playerTurnMenu(Player player, PlayCardData playCardData, boolean toDiscard) {
        clear();
        return new AdvancedPlayerTurnMenu(player, playCardData, toDiscard).start();
    }

    @Override
    public void endGameDisplay(
            Map<AbstractPlayerController, Integer> playerScores, GameResult result) {
        new GameOverDisplay().display();
        new EndGameScoreBoardDisplay(playerScores).display();
        new WinnerResultDisplay(playerScores, result).display();
        sleep(10_000, true);
    }

    @Override
    public void byeByeDisplay() {
        new ThankYouDisplay().display();
        sleep(10_000, true);
    }
}
