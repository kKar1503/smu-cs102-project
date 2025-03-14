package parade.common.state.client;

import parade.player.IPlayer;

import java.io.Serial;

public class PlayerLobbyCloseData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -3424088172272211310L;

    private final String lobbyName;
    private final String lobbyPassword;

    public PlayerLobbyCloseData(IPlayer caller, String lobbyName, String lobbyPassword) {
        super(caller, ClientAction.LOBBY_CLOSE);
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
