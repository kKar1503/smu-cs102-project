package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;

public class ClientLobbyRequestListData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -2954383229739761507L;

    public ClientLobbyRequestListData(Player caller) {
        super(caller, ClientAction.LOBBY_REQUEST_LIST);
    }
}
