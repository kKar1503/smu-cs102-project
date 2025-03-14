package parade.common;

import parade.player.IPlayer;

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
    private final List<IPlayer> players = new ArrayList<>();
    private final boolean isPublic;
    private final IPlayer owner;

    public Lobby(String name, int maxPlayers, IPlayer owner, boolean isPublic) {
        this.name = name;
        this.password = null;
        this.maxPlayers = maxPlayers;
        this.owner = owner;
        this.isPublic = isPublic;
    }

    public Lobby(String name, String password, int maxPlayers, IPlayer owner, boolean isPublic) {
        this.name = name;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.owner = owner;
        this.isPublic = isPublic;
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

    public IPlayer getOwner() {
        return owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void addPlayer(IPlayer player) {
        if (players.size() < maxPlayers) {
            players.add(player);
        } else {
            throw new IllegalStateException("Lobby is full");
        }
    }

    public void removePlayer(IPlayer player) {
        players.remove(player);
    }

    public List<IPlayer> getPlayers() {
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
