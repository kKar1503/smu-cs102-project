package parade.common.state.client;

import parade.player.IPlayer;

import java.io.Serial;
import java.io.Serializable;

public abstract class AbstractClientData implements Serializable {
    @Serial private static final long serialVersionUID = -3525120046343996754L;

    private final IPlayer caller;
    private final ClientAction clientAction;

    public AbstractClientData(IPlayer caller, ClientAction clientAction) {
        this.caller = caller;
        this.clientAction = clientAction;
    }

    public IPlayer getCaller() {
        return caller;
    }

    public ClientAction getClientAction() {
        return clientAction;
    }
}
