package parade.common.state.client;

import parade.player.IPlayer;

import java.io.Serial;

public class PlayerLobbyLeaveData extends AbstractClientData {
    @Serial private static final long serialVersionUID = 1076051152364398499L;

    private final String lobbyName;

    public PlayerLobbyLeaveData(IPlayer caller, String lobbyName) {
        super(caller, ClientAction.LOBBY_LEAVE);
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
