package parade.common.state.client;

import parade.common.Player;

import java.io.Serial;

public class ClientLobbyCloseData extends AbstractClientData {
    @Serial private static final long serialVersionUID = -3424088172272211310L;

    private final String lobbyId;

    public ClientLobbyCloseData(Player caller, String lobbyId) {
        super(caller, ClientAction.LOBBY_CLOSE);
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    @Override
    public String toString() {
        return "ClientLobbyCloseData{lobbyId='" + lobbyId + "', super=" + super.toString() + '}';
    }
}
