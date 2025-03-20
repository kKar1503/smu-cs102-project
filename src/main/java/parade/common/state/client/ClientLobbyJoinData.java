package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;

public class ClientLobbyJoinData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -2464395654042284252L;

    private final String lobbyName;
    private final String lobbyPassword;

    public ClientLobbyJoinData(Player caller, String lobbyName, String lobbyPassword) {
        super(caller, ClientAction.LOBBY_JOIN);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getLobbyPassword() {
        return lobbyPassword;
    }
}
