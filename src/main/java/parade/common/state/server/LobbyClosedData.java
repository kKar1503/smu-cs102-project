package parade.common.state.server;

import java.io.Serial;

public class LobbyClosedData extends AbstractServerData {
    @Serial private static final long serialVersionUID = 5995066579914812638L;

    private final String lobbyId;
    private final String reason;

    public LobbyClosedData(String lobbyId, String reason) {
        super(ServerAction.LOBBY_CLOSED);
        this.lobbyId = lobbyId;
        this.reason = reason;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getReason() {
        return reason;
    }
}
