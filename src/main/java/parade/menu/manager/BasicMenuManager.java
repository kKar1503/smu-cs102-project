package parade.menu.manager;

import parade.menu.base.MenuResource;
import parade.menu.base.MenuResource.MenuResourceType;
import parade.menu.display.*;
import parade.menu.menu.*;
import parade.menu.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

/**
 * A basic text-based implementation of the client renderer for local gameplay. Responsible for
 * displaying game state and prompting the user via console.
 */
public class BasicMenuManager extends AbstractMenuManager {
    /**
     * Renders the welcome banner for the game. Throws an exception if file is missing.
     *
     * @throws IllegalStateException if the resource file cannot be found
     */
    @Override
    public void welcomeDisplay() throws IllegalStateException {
        new DynamicSeparator("✨ Welcome to Parade! ✨", Ansi.PURPLE::apply);
    }

    @Override
    public MainMenuOption mainMenu() {
        return new AsciiMainMenu().start();
    }

    /**
     * Displays the turn information for a player including hand, board, and parade.
     *
     * @param player the current player
     * @param playCardData the data for the current turn
     * @param toDiscard indicates if the player should discard a card
     */
    @Override
    public int renderPlayerTurn(Player player, PlayCardData playCardData, boolean toDiscard) {
        clear();
        return new BasicPlayerTurnMenu(player, playCardData, toDiscard).start();
    }

    /**
     * Renders the game ending screen with animation and final scores.
     *
     * @param playerScores final score map of all players
     */
    @Override
    public void renderEndGame(Map<AbstractPlayerController, Integer> playerScores) {
        String asciiFinal = MenuResource.get(MenuResourceType.ASCII_FINAL);
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

    /**
     * Renders an animated dice-rolling block with shaking effect. This is purely visual and does
     * not determine any game outcome.
     */
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

    /** Renders a simple farewell message at the end of the game session. */
    @Override
    public void renderBye() {
        printlnFlush(System.lineSeparator() + "THANK YOU FOR PLAYING! SEE YOU NEXT TIME!");
    }
}
