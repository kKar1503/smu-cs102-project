package parade.common.state.client;

import parade.player.IPlayer;

import java.io.Serial;

public class PlayerConnectData extends AbstractClientData {
    @Serial private static final long serialVersionUID = 3344066352483663926L;

    public PlayerConnectData(IPlayer caller) {
        super(caller, ClientAction.CONNECT);
    }
}
