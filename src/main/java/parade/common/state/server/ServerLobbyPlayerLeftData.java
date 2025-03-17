package parade.common.state.server;

import parade.common.Player;

import java.io.Serial;

public class ServerLobbyPlayerLeftData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -1920992070116299579L;

    private final Player playerLeft;

    public ServerLobbyPlayerLeftData(Player playerLeft) {
        super(ServerAction.LOBBY_PLAYER_LEFT);
        this.playerLeft = playerLeft;
    }

    public Player getPlayerLeft() {
        return playerLeft;
    }
}
