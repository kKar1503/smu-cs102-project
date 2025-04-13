package parade.menu.display;

import parade.menu.base.AbstractDisplay;
import parade.player.controller.AbstractPlayerController;

import java.util.*;

public class EndGameScoreBoardDisplay extends AbstractDisplay {
    private static final int PLAYER_COL_WIDTH = 32;
    private static final int SCORE_COL_WIDTH = 9;

    private final HorizontallyCentralisedDisplay scoreboard;

    public EndGameScoreBoardDisplay(Map<AbstractPlayerController, Integer> playerScores) {
        List<String> table = new ArrayList<>();
        table.add(String.format("┌%s┐", "─".repeat(PLAYER_COL_WIDTH + 2 + SCORE_COL_WIDTH + 3)));
        table.add(
                String.format(
                        "│ %-" + PLAYER_COL_WIDTH + "s │ %-" + SCORE_COL_WIDTH + "s │",
                        "Player",
                        "Score"));
        table.add(
                "├"
                        + "─".repeat(PLAYER_COL_WIDTH + 2)
                        + "┼"
                        + "─".repeat(SCORE_COL_WIDTH + 2)
                        + "┤");

        for (Map.Entry<AbstractPlayerController, Integer> entry : playerScores.entrySet()) {
            table.add(
                    String.format(
                            "│ %-" + PLAYER_COL_WIDTH + "s │ %" + SCORE_COL_WIDTH + "d │",
                            entry.getKey().getPlayer().getName(),
                            entry.getValue()));
        }

        table.add(
                "└"
                        + "─".repeat(PLAYER_COL_WIDTH + 2)
                        + "┴"
                        + "─".repeat(SCORE_COL_WIDTH + 2)
                        + "┘");
        this.scoreboard = new HorizontallyCentralisedDisplay(table.toArray(String[]::new));
    }

    @Override
    public void display() {
        scoreboard.display();
    }
}
