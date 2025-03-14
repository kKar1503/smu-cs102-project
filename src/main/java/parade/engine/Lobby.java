package parade.engine;

import parade.controller.IPlayer;

import java.util.List;

public class Lobby {
    private final List<IPlayer> players;
    private int currentPlayerIdx = 0;

    public Lobby(List<IPlayer> players) {
        this.players = players;
    }

    /**
     * Gets the current player.
     *
     * @return The current player.
     */
    public IPlayer getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    /** Increments the index of the current player to the next player in the list. */
    public void nextPlayer() {
        currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
    }
}
