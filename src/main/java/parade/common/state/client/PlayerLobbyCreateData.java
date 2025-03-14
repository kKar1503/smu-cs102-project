package parade.common.state.client;

import parade.player.IPlayer;
import parade.player.Player;

import java.io.Serial;

public class PlayerLobbyCreateData extends AbstractClientData {
    @Serial private static final long serialVersionUID = 7081463059860918662L;

    private final String lobbyName;
    private final String lobbyPassword;
    private final int maxPlayers;

    public PlayerLobbyCreateData(
            IPlayer caller, String lobbyName, String lobbyPassword, int maxPlayers) {
        super(caller, ClientAction.LOBBY_CREATE);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
        this.maxPlayers = maxPlayers;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getLobbyPassword() {
        return lobbyPassword;
    }
}
