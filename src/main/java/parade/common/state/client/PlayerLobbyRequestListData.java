package parade.common.state.client;

import parade.player.IPlayer;

import java.io.Serial;

public class PlayerLobbyRequestListData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -2954383229739761507L;

    public PlayerLobbyRequestListData(IPlayer caller) {
        super(caller, ClientAction.LOBBY_REQUEST_LIST);
    }
}
