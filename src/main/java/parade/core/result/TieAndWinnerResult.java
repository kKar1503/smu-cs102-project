package parade.core.result;

import parade.player.controller.AbstractPlayerController;

public class TieAndWinnerResult implements GameResult {
    private final AbstractPlayerController player;

    protected TieAndWinnerResult(AbstractPlayerController player) {
        this.player = player;
    }

    public AbstractPlayerController getPlayer() {
        return player;
    }
}
