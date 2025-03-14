package parade.common.state.server;

import java.io.Serial;

public class LobbyCreateAckData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -2761035513903696590L;

    private final String lobbyId;
    private final boolean successful;

    /**
     * Message to be displayed to the user, e.g. "Lobby created successfully" or "Lobby creation
     * failed: {reason}"
     */
    private final String message;

    public LobbyCreateAckData(String lobbyId, boolean successful, String message) {
        super(ServerAction.LOBBY_CREATE_ACK);
        this.lobbyId = lobbyId;
        this.successful = successful;
        this.message = message;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }
}
