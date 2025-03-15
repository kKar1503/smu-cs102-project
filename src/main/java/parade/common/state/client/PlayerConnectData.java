package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;

public class PlayerConnectData extends AbstractClientData {
    @Serial private static final long serialVersionUID = 3344066352483663926L;

    public PlayerConnectData(Player caller) {
        super(caller, ClientAction.CONNECT);
    }
}
