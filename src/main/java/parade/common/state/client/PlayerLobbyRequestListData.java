package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;

public class PlayerLobbyRequestListData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -2954383229739761507L;

    public PlayerLobbyRequestListData(Player caller) {
        super(caller, ClientAction.LOBBY_REQUEST_LIST);
    }
}
