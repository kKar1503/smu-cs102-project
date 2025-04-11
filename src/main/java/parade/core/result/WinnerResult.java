package parade.core.result;

import parade.player.controller.AbstractPlayerController;

public class WinnerResult extends AbstractResult {
    private AbstractPlayerController player;

    protected WinnerResult(AbstractPlayerController player) {
        this.player = player;
    }

    public AbstractPlayerController getPlayer() {
        return player;
    }
}
