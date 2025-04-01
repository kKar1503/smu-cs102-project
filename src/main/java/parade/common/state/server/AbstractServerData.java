package parade.common.state.server;

import java.io.Serial;
import java.io.Serializable;

public abstract class AbstractServerData implements Serializable {
    @Serial private static final long serialVersionUID = -6034683007884674756L;

    private final ServerAction serverAction;

    public AbstractServerData(ServerAction serverAction) {
        this.serverAction = serverAction;
    }

    public ServerAction getServerAction() {
        return serverAction;
    }

    @Override
    public String toString() {
        return "AbstractServerData{serverAction=" + serverAction + '}';
    }
}
