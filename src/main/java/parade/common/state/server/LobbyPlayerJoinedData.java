package parade.common.state.server;

import parade.player.IPlayer;

import java.io.Serial;

public class LobbyPlayerJoinedData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -3219009780382677686L;

    private final IPlayer playerJoined;

    public LobbyPlayerJoinedData(IPlayer playerJoined) {
        super(ServerAction.LOBBY_PLAYER_JOINED);
        this.playerJoined = playerJoined;
    }

    public IPlayer getPlayerJoined() {
        return playerJoined;
    }
}
