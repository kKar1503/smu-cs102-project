package parade.core.result;

import parade.player.Player;
import parade.player.controller.AbstractPlayerController;

import java.util.*;

public class DeclareWinner {

    public GameResult evaluateScores(Map<AbstractPlayerController, Integer> playerScores) {

        int lowestScore = Integer.MAX_VALUE;
        List<AbstractPlayerController> resultList = new ArrayList<>();
        boolean tieScoreWithWinner = false;

        for (AbstractPlayerController controller : playerScores.keySet()) {
            Player player = controller.getPlayer();
            int currentScore = playerScores.get(controller);
            if (currentScore < lowestScore) {
                lowestScore = currentScore;
                resultList
                        .clear(); // Remove all existing players if there is one with a lower score
                resultList.add(controller);
                tieScoreWithWinner = false;
            } else if (currentScore == lowestScore) {
                tieScoreWithWinner = true;
                // Check if current player and potential winners all have the same number of cards
                boolean sameCardCount = true;
                for (AbstractPlayerController resultController : resultList) {
                    Player resultPlayer = resultController.getPlayer();
                    if (resultPlayer.getBoard().size() != player.getBoard().size()) {
                        sameCardCount = false;
                        break;
                    }
                }
                // If everyone have the same score and tie, add the player into the list
                if (sameCardCount) {
                    resultList.add(controller);
                    continue;
                }
                for (AbstractPlayerController resultController : resultList) {
                    Player resultPlayer = resultController.getPlayer();
                    // If player has lesser cards, there will not be an overall tie
                    if (player.getBoard().size() < resultPlayer.getBoard().size()) {
                        resultList.clear();
                        resultList.add(controller);
                    }
                }
            }
        }

        if (resultList.size() == 1 && !tieScoreWithWinner) {
            return new WinnerResult(resultList.get(0));
        } else if (resultList.size() == 1 && tieScoreWithWinner) {
            return new TieAndWinnerResult(resultList.get(0));
        } else {
            return new TieAndNoWinnerResult(resultList);
        }
    }
}
