package parade.menu.manager;

import parade.menu.base.MenuResource;
import parade.menu.display.*;
import parade.menu.menu.*;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

/**
 * AdvancedClientRenderer provides advanced rendering capabilities for the Parade game. It outputs
 * styled game content to the console.
 */
public class AdvancedMenuManager extends AbstractMenuManager {
    /**
     * Renders a stylized welcome message from an ASCII art file. Throws an exception if the file
     * cannot be found. Also displays a sample Parade card.
     */
    @Override
    public void welcomeDisplay() throws IllegalStateException {
        new AsciiWelcome().display();
    }

    /**
     * Displays the current state of a player's turn, including: - Drawn card (if any) - Parade line
     * - Player's board (sorted) - Player's hand Prompts the player to select a card to play.
     *
     * @param player The player whose turn is being rendered.
     * @param playCardData The data object containing information about the game state.
     */
    @Override
    public int renderPlayerTurn(Player player, PlayCardData playCardData, boolean toDiscard) {
        clear();
        return new AdvancedPlayerTurnMenu(player, playCardData, toDiscard).start();
    }

    /** Displays a farewell message when the game ends. */
    @Override
    public void renderBye() {
        printlnFlush(System.lineSeparator() + "THANK YOU FOR PLAYING! SEE YOU NEXT TIME!");
    }

    @Override
    public void renderRoll(int diceRoll1, int diceRoll2, List<Player> players) {
        clear();
        new Dice(diceRoll1, diceRoll2).display();
        sleep();
        clear();
        new BoxedText(
                        String.format(
                                "%s will play first!",
                                getChosenPlayerFromDice(diceRoll1 + diceRoll2, players)),
                        2,
                        true)
                .display();
        sleep();
    }

    /**
     * Renders the game ending screen with animation and final scores.
     *
     * @param playerScores final score map of all players
     */
    @Override
    public void renderEndGame(Map<AbstractPlayerController, Integer> playerScores) {
        String asciiFinal = MenuResource.get(MenuResource.MenuResourceType.ASCII_FINAL);
        for (int i = 0; i < 30; i++) {
            clear();
            printlnFlush(Ansi.PURPLE.apply(asciiFinal));
            sleep(100);
        }

        for (int i = 0; i < 6; i++) {
            clear();
            printlnFlush(Ansi.PURPLE.apply(asciiFinal));
        }

        int playerColWidth = 32;
        int scoreColWidth = 9;
        String header =
                String.format(
                        "        ┌%s┐%n"
                                + "        │ %-"
                                + playerColWidth
                                + "s │ %-"
                                + scoreColWidth
                                + "s │%n"
                                + "        ├%s┤",
                        "─".repeat(playerColWidth + 2 + scoreColWidth + 3),
                        "Player",
                        "Score",
                        "─".repeat(playerColWidth + 2) + "┼" + "─".repeat(scoreColWidth + 2));
        println(header);

        for (Map.Entry<AbstractPlayerController, Integer> entry : playerScores.entrySet()) {
            printf(
                    "        │ %-" + playerColWidth + "s │ %" + scoreColWidth + "d │%n",
                    entry.getKey().getPlayer().getName(),
                    entry.getValue());
        }

        println(
                "        └"
                        + "─".repeat(playerColWidth + 2)
                        + "┴"
                        + "─".repeat(scoreColWidth + 2)
                        + "┘");
        flush();
    }
}
