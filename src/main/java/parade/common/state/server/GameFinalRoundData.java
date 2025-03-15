package parade.common.state.server;


import java.io.Serial;

public class GameFinalRoundData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -7063414929184528954L;

    public GameFinalRoundData() {
        super(ServerAction.GAME_FINAL_ROUND);
    }
}
