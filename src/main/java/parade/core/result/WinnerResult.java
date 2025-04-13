package parade.core.result;

import parade.player.controller.AbstractPlayerController;

public class WinnerResult implements GameResult {
    private final AbstractPlayerController player;

    protected WinnerResult(AbstractPlayerController player) {
        this.player = player;
    }

    public AbstractPlayerController getPlayer() {
        return player;
    }
}
