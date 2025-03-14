package parade.common.state.server;

import parade.player.IPlayer;

import java.io.Serial;

public class LobbyPlayerLeftData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -1920992070116299579L;

    private final IPlayer playerLeft;

    public LobbyPlayerLeftData(IPlayer playerLeft) {
        super(ServerAction.LOBBY_PLAYER_LEFT);
        this.playerLeft = playerLeft;
    }

    public IPlayer getPlayerLeft() {
        return playerLeft;
    }
}
