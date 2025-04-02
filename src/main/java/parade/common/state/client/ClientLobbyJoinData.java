package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;

public class ClientLobbyJoinData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -2464395654042284252L;

    private final String lobbyId;
    private final String lobbyPassword;

    public ClientLobbyJoinData(Player caller, String lobbyId, String lobbyPassword) {
        super(caller, ClientAction.LOBBY_JOIN);
        this.lobbyId = lobbyId;
        this.lobbyPassword = lobbyPassword;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getLobbyPassword() {
        return lobbyPassword;
    }
}
