package parade.result;

import parade.player.controller.AbstractPlayerController;

public class TieAndWinnerResult extends AbstractResult {
    private AbstractPlayerController player;

    protected TieAndWinnerResult(AbstractPlayerController player) {
        this.player = player;
    }

    public AbstractPlayerController getPlayer() {
        return player;
    }
}
