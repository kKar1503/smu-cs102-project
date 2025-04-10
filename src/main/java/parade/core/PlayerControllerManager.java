package parade.core;

import parade.player.Player;
import parade.player.controller.AbstractPlayerController;

import java.util.*;

class PlayerControllerManager implements Iterator<AbstractPlayerController> {
    private final List<AbstractPlayerController> lobby;
    private int currentPlayerIdx = 0;

    PlayerControllerManager() {
        this.lobby = new ArrayList<>(6);
    }

    /**
     * Adds a player controller to the list.
     *
     * @param playerController The player controller to add.
     */
    void add(AbstractPlayerController playerController) {
        lobby.add(playerController);
    }

    /**
     * Increments the index of the current player to the next player in the list and returns the
     * next player controller.
     *
     * @return The next player controller.
     */
    @Override
    public AbstractPlayerController next() {
        return lobby.get(nextIndex());
    }

    private int nextIndex() {
        int thisIndex = currentPlayerIdx;
        currentPlayerIdx = ++currentPlayerIdx % lobby.size();
        return thisIndex;
    }

    @Override
    public boolean hasNext() {
        return currentPlayerIdx < lobby.size();
    }

    boolean remove(AbstractPlayerController playerController) {
        return lobby.remove(playerController);
    }

    AbstractPlayerController remove(int index) {
        return lobby.remove(index);
    }

    boolean remove(Player player) {
        for (AbstractPlayerController playerController : lobby) {
            if (playerController.getPlayer().equals(player)) {
                return remove(playerController);
            }
        }
        return false;
    }

    List<AbstractPlayerController> getPlayerControllers() {
        return Collections.unmodifiableList(lobby);
    }

    List<Player> getPlayers() {
        return lobby.stream().map(AbstractPlayerController::getPlayer).toList();
    }

    int size() {
        return lobby.size();
    }

    boolean isReady() {
        return lobby.size() >= 2;
    }

    boolean isFull() {
        return lobby.size() == 6;
    }

    boolean isEmpty() {
        return lobby.isEmpty();
    }
}
