package parade.common.state.server;

import java.io.Serial;

public class ServerGameFinalRoundData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -7063414929184528954L;

    public ServerGameFinalRoundData() {
        super(ServerAction.GAME_FINAL_ROUND);
    }
}
