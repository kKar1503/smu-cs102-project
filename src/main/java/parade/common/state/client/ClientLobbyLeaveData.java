package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;

public class ClientLobbyLeaveData extends AbstractClientData {
    @Serial private static final long serialVersionUID = 1076051152364398499L;

    private final String lobbyId;

    public ClientLobbyLeaveData(Player caller, String lobbyId) {
        super(caller, ClientAction.LOBBY_LEAVE);
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
