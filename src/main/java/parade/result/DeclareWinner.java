package parade.result;

import parade.player.IPlayer;

import java.util.*;

public class DeclareWinner {

    public AbstractResult evaluateScores(Map<IPlayer,Integer> playerScores) {

        int lowestScore = Integer.MAX_VALUE;
        List<IPlayer> resultList = new ArrayList<>();
        boolean tieScoreWithWinner = false;

        for (IPlayer player : playerScores.keySet()) {

            int currentScore = playerScores.get(player);
            if (currentScore < lowestScore) {
                lowestScore = currentScore; 
                resultList.clear(); // Remove all existing players if there is one with a lower score
                resultList.add(player);
                tieScoreWithWinner = false;
            } else if (currentScore == lowestScore) {
                // Check if current player and potential winners all have the same number of cards
                boolean sameCardCount = true;
                for (IPlayer resultPlayer : resultList) {
                    if (resultPlayer.getBoard().size() != player.getBoard().size()) {
                        sameCardCount = false;
                        break;
                    }
                }
                // If everyone have the same score and tie, add the player into the list
                if (sameCardCount) {
                    resultList.add(player);
                    continue;
                }
                for (IPlayer resultPlayer : resultList) {
                    // If player has lesser cards, there will not be an overall tie
                    if (player.getBoard().size() < resultPlayer.getBoard().size()) {
                        resultList.clear();
                        resultList.add(player);
                        tieScoreWithWinner = true;
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
