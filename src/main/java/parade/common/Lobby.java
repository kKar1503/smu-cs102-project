package parade.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lobby implements Serializable {
    @Serial private static final long serialVersionUID = -3130843115602392208L;

    private final UUID id = UUID.randomUUID();
    private final String name;
    private final int maxPlayers;
    private final transient String password;
    private final List<Player> players = new ArrayList<>();
    private final boolean isPublic;
    private final Player owner;
    private LobbyStatus status;

    public Lobby(String name, int maxPlayers, Player owner, boolean isPublic) {
        this.name = name;
        this.password = null;
        this.maxPlayers = maxPlayers;
        this.owner = owner;
        this.isPublic = isPublic;
        this.status = LobbyStatus.WAITING_FOR_PLAYERS;
    }

    public Lobby(String name, int maxPlayers, Player owner, boolean isPublic, LobbyStatus status) {
        this.name = name;
        this.password = null;
        this.maxPlayers = maxPlayers;
        this.owner = owner;
        this.isPublic = isPublic;
        this.status = status;
    }

    public Lobby(String name, String password, int maxPlayers, Player owner, boolean isPublic) {
        this.name = name;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.owner = owner;
        this.isPublic = isPublic;
        this.status = LobbyStatus.WAITING_FOR_PLAYERS;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * When transmitted over the network, the password is not sent.
     *
     * @return the password of the lobby, null if no password is set
     */
    public String getPassword() {
        return password;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public LobbyStatus getStatus() {
        return status;
    }

    public void setStatus(LobbyStatus status) {
        this.status = status;
    }

    public void addPlayer(Player player) {
        if (players.size() < maxPlayers) {
            players.add(player);
        } else {
            throw new IllegalStateException("Lobby is full");
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lobby l && id.equals(l.id);
    }
}
