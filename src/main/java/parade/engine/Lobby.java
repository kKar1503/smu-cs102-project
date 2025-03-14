package parade.engine;

import parade.player.Player;
import java.util.List;

public class Lobby {
    private final List<Player> players;
    private int currentPlayerIdx = 0;

    public Lobby(List<Player> players) {
        this.players = players;
    }

    /**
     * Gets the current player.
     *
     * @return The current player.
     */
    protected Player getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    /** Increments the index of the current player to the next player in the list. */
    protected void nextPlayer() {
        currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
    }
}
