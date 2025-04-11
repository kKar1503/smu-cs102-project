package parade.core.result;

import parade.player.controller.AbstractPlayerController;

import java.util.*;

public class TieAndNoWinnerResult extends AbstractResult {
    private List<AbstractPlayerController> playerList;

    protected TieAndNoWinnerResult(List<AbstractPlayerController> playerList) {
        this.playerList = playerList;
    }

    public List<AbstractPlayerController> getPlayers() {
        return playerList;
    }
}
