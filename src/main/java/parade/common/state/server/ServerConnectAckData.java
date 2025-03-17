package parade.common.state.server;

import java.io.Serial;

public class ServerConnectAckData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -892047633303556973L;

    private final boolean accepted;
    private final String message;

    public ServerConnectAckData(boolean accepted, String message) {
        super(ServerAction.CONNECT_ACK);
        this.accepted = accepted;
        this.message = message;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getMessage() {
        return message;
    }
}
