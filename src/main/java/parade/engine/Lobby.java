package parade.engine;

import parade.controller.IPlayerController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lobby<T extends IPlayerController> {
    private final List<T> playerControllers = new ArrayList<>();
    private int currentPlayerIdx = 0;

    /**
     * Gets the current player controller.
     *
     * @return The current player controller.
     */
    public T getCurrentPlayerController() {
        return playerControllers.get(currentPlayerIdx);
    }

    public boolean removePlayerController(T playerController) {
        return playerControllers.remove(playerController);
    }

    public T removePlayerController(int index) {
        return playerControllers.remove(index);
    }

    public List<T> getPlayerControllers() {
        return Collections.unmodifiableList(playerControllers);
    }

    /** Increments the index of the current player to the next player in the list. */
    public void nextPlayer() {
        currentPlayerIdx = (currentPlayerIdx + 1) % playerControllers.size();
    }
}
