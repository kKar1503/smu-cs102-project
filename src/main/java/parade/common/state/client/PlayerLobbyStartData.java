package parade.common.state.client;

import parade.player.IPlayer;

import java.io.Serial;

public class PlayerLobbyStartData extends AbstractClientData {
    @Serial private static final long serialVersionUID = 8186840607843712754L;

    private final String lobbyName;
    private final String lobbyPassword;

    public PlayerLobbyStartData(IPlayer caller, String lobbyName, String lobbyPassword) {
        super(caller, ClientAction.LOBBY_START);
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
