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

    void add(AbstractPlayerController playerController) {
        lobby.add(playerController);
    }

    @Override
    public AbstractPlayerController next() {
        return lobby.get(nextIndex(true));
    }

    public AbstractPlayerController peek() {
        return lobby.get(nextIndex(false));
    }

    public void setCurrentPlayerIdx(int idx) {
        currentPlayerIdx = idx % lobby.size();
    }

    private int nextIndex(boolean increment) {
        int thisIndex = currentPlayerIdx;
        if (increment) {
            currentPlayerIdx = ++currentPlayerIdx % lobby.size();
        }
        return thisIndex;
    }

    @Override
    public boolean hasNext() {
        return currentPlayerIdx < lobby.size();
    }

    boolean remove(AbstractPlayerController playerController) {
        return lobby.remove(playerController);
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
}
