package parade.common.state.server;

import parade.common.Player;

import java.io.Serial;

public class LobbyPlayerJoinedData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -3219009780382677686L;

    private final Player playerJoined;

    public LobbyPlayerJoinedData(Player playerJoined) {
        super(ServerAction.LOBBY_PLAYER_JOINED);
        this.playerJoined = playerJoined;
    }

    public Player getPlayerJoined() {
        return playerJoined;
    }
}
