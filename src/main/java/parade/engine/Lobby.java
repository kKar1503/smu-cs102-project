package parade.engine;

import parade.controller.IPlayerController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lobby {
    private final List<IPlayerController> players = new ArrayList<>();
    private int currentPlayerIdx = 0;

    public Lobby() {}

    /**
     * Gets the current player.
     *
     * @return The current player.
     */
    public IPlayerController getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    public List<IPlayerController> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /** Increments the index of the current player to the next player in the list. */
    public void nextPlayer() {
        currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
    }
}
