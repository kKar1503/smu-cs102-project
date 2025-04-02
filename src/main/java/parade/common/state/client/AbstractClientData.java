package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;
import java.io.Serializable;

public abstract class AbstractClientData implements Serializable {
    @Serial private static final long serialVersionUID = -3525120046343996754L;

    private final Player caller;
    private final ClientAction clientAction;

    public AbstractClientData(Player caller, ClientAction clientAction) {
        this.caller = caller;
        this.clientAction = clientAction;
    }

    public Player getCaller() {
        return caller;
    }

    public ClientAction getClientAction() {
        return clientAction;
    }

    @Override
    public String toString() {
        return "AbstractClientData{caller=" + caller + ", clientAction=" + clientAction + '}';
    }
}
