package parade.common.state.server;

import java.io.Serial;

public class ServerLobbyJoinAckData extends AbstractServerData {
    @Serial private static final long serialVersionUID = 5799528422932046002L;

    private final String lobbyId;
    private final boolean successful;

    /**
     * Message to be displayed to the user. This can be an error message or a success message.
     *
     * <p>If the lobby join is unsuccessful, the message contains the reason, i.e., "Lobby is full".
     */
    private final String message;

    public ServerLobbyJoinAckData(String lobbyId, boolean successful, String message) {
        super(ServerAction.LOBBY_JOIN_ACK);
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
