package parade.common.state.client;

import parade.player.IPlayer;

import java.io.Serial;

public class PlayerLobbyJoinData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -2464395654042284252L;

    private final String lobbyName;
    private final String lobbyPassword;

    public PlayerLobbyJoinData(IPlayer caller, String lobbyName, String lobbyPassword) {
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
