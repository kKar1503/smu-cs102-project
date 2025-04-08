package parade.result;

import parade.player.IPlayer;

public class WinnerResult extends AbstractResult {
    private IPlayer player;

    protected WinnerResult(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return player;
    }
}
