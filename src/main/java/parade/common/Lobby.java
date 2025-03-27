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
    private final int minPlayers;
    private final int maxPlayers;
    private final String password;
    private final List<Player> players = new ArrayList<>();
    private final Player owner;

    public Lobby(String name, int minPlayers, int maxPlayers, Player owner) {
        this(name, null, minPlayers, maxPlayers, owner);
    }

    public Lobby(String name, String password, int minPlayers, int maxPlayers, Player owner) {
        this.name = name;
        this.password = password;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.owner = owner;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public int getMinPlayers() {
        return minPlayers;
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
        return password == null;
    }

    public void add(Player player) throws IllegalArgumentException, IllegalStateException {
        if (players.size() >= maxPlayers) {
            throw new IllegalStateException("Lobby is full");
        }
        if (players.contains(player)) {
            throw new IllegalArgumentException("Player is already in the lobby");
        }
        players.add(player);
    }

    public boolean remove(Player player) {
        return players.remove(player);
    }

    public Player remove(int i) {
        return players.remove(i);
    }

    public List<Player> get() {
        return Collections.unmodifiableList(players);
    }

    public Player get(int index) {
        return players.get(index);
    }

    public int size() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    public boolean isReady() {
        return players.size() >= minPlayers;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lobby l && id.equals(l.id);
    }
}
