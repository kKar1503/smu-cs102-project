package parade.menu.manager;

import parade.menu.display.DynamicSeparator;
import parade.menu.menu.*;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

/**
 * A debug text-based implementation of the client renderer for development. Responsible for
 * displaying game state and prompting the user via console.
 */
public class DebugMenuManager extends AbstractMenuManager {
    @Override
    public void welcomeDisplay() throws IllegalStateException {
        new DynamicSeparator("Welcome to Parade!", Ansi.PURPLE::apply).display();
    }

    @Override
    public int renderPlayerTurn(Player player, PlayCardData playCardData, boolean toDiscard) {
        return new DebugPlayerTurnMenu(player, playCardData, toDiscard).start();
    }

    @Override
    public void renderEndGame(Map<AbstractPlayerController, Integer> playerScores) {
        println("Game Over!");
        for (Map.Entry<AbstractPlayerController, Integer> entry : playerScores.entrySet()) {
            println(entry.getKey().getPlayer().getName() + ": " + entry.getValue());
        }
        flush();
    }

    @Override
    public void renderRoll(int diceRoll1, int diceRoll2, List<Player> players) {
        println("Rolling dice..");
        println("Dice roll 1: " + diceRoll1);
        println("Dice roll 2: " + diceRoll2);
        flush();
        super.renderRoll(diceRoll1, diceRoll2, players);
    }
}
