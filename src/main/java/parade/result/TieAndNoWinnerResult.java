package parade.result;

import parade.player.IPlayer;

import java.util.*;

public class TieAndNoWinnerResult extends AbstractResult{
    private List<IPlayer> playerList;

    protected TieAndNoWinnerResult(List<IPlayer> playerList) {
        this.playerList = playerList;
    }

    public List<IPlayer> getPlayers() {
        return playerList;
    }
}
