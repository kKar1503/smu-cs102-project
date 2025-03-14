package parade.common.state.server;

import parade.common.Lobby;

import java.io.Serial;
import java.util.Arrays;

public class LobbyListData extends AbstractServerData {
    @Serial private static final long serialVersionUID = 5522050725733716543L;

    public final Lobby[] lobbies;

    public LobbyListData(Lobby[] lobbies) {
        super(ServerAction.LOBBY_LIST);
        this.lobbies = Arrays.copyOf(lobbies, lobbies.length);
    }

    public Lobby[] getLobbies() {
        return lobbies;
    }
}
