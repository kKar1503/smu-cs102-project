package parade.common.state.client;

import java.io.Serial;

/**
 * Poison pill is a reserved {@link AbstractClientData} inheritor reserved for the server.
 *
 * <p>It is used to signal the consumer threads that the client has disconnected and that the server
 * should stop waiting for any further messages from this client.
 */
public class ClientPoisonPill extends AbstractClientData {
    @Serial private static final long serialVersionUID = -2471866246517384690L;

    public ClientPoisonPill() {
        super(null, ClientAction.LOBBY_START);
    }
}
