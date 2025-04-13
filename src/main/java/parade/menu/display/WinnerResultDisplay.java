package parade.menu.display;

import parade.core.result.*;
import parade.menu.base.AbstractDisplay;
import parade.player.controller.AbstractPlayerController;

import java.util.List;
import java.util.Map;

public class WinnerResultDisplay extends AbstractDisplay {
    private final Map<AbstractPlayerController, Integer> playerScores;
    private final GameResult result;

    public WinnerResultDisplay(
            Map<AbstractPlayerController, Integer> playerScores, GameResult result) {
        this.playerScores = playerScores;
        this.result = result;
    }

    @Override
    public void display() {
        switch (result) {
            case WinnerResult win -> {
                printfFlush(
                        "%s wins with %d points!%n",
                        win.getPlayer().getPlayer().getName(), playerScores.get(win.getPlayer()));
            }
            case TieAndWinnerResult tie -> {
                printfFlush(
                        "Tie in score of %d points but %s wins with lesser number of cards%n",
                        playerScores.get(tie.getPlayer()), tie.getPlayer().getPlayer().getName());
            }
            case TieAndNoWinnerResult overallTie -> {
                println("Overall tie with no winners");
                List<AbstractPlayerController> players = overallTie.getPlayers();
                int numPlayers = players.size();
                int score = playerScores.get(players.getFirst());
                for (int i = 0; i < numPlayers - 1; i++) {
                    print(players.get(i).getPlayer().getName() + ", ");
                }
                printf(
                        "%s have the same score of %d points and same number of cards.%n",
                        players.get(numPlayers - 1).getPlayer().getName(), score);
                flush();
            }
            default -> printlnFlush("Error retrieving result");
        }
    }
}
