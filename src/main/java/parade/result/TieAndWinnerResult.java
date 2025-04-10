package parade.result;

import parade.player.IPlayer;

import java.util.*;

public class TieAndWinnerResult extends AbstractResult{
    private IPlayer player;

    protected TieAndWinnerResult(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return player;
    }
}
