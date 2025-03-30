package parade.engine;

import parade.common.Lobby;
import parade.common.Player;
import parade.controller.IPlayerController;

import java.util.*;

class PlayerControllerManager<T extends IPlayerController> implements Iterator<T> {
    private final Map<Player, T> playerControllersMap;
    private final Lobby lobby;
    private int currentPlayerIdx = 0;

    PlayerControllerManager(Lobby lobby) {
        if (lobby.size() > 0) {
            throw new IllegalArgumentException(
                    "Lobby must be empty to create a PlayerControllerManager");
        }
        this.lobby = lobby;
        this.playerControllersMap = new HashMap<>(lobby.getMaxPlayers());
    }

    /**
     * Adds a player controller to the list.
     *
     * @param playerController The player controller to add.
     * @throws IllegalStateException if there are already 6 players in the list.
     * @throws IllegalArgumentException if the player controller already exists in the list.
     */
    void add(T playerController) throws IllegalArgumentException, IllegalStateException {
        if (playerControllersMap.containsValue(playerController)) {
            throw new IllegalArgumentException(
                    "PlayerController already exists in the list: " + playerController);
        }
        Player p = playerController.getPlayer();
        playerControllersMap.put(p, playerController);
        lobby.add(p);
    }

    /**
     * Increments the index of the current player to the next player in the list and returns the
     * next player controller.
     *
     * @return The next player controller.
     */
    @Override
    public T next() {
        return playerControllersMap.get(lobby.get(currentPlayerIdx++ % lobby.size()));
    }

    @Override
    public boolean hasNext() {
        return currentPlayerIdx < lobby.size();
    }

    boolean remove(T playerController) {
        // TODO: fix the remove to realign the index
        Player p = playerController.getPlayer();
        boolean exists = playerControllersMap.remove(p, playerController);
        if (exists) {
            lobby.remove(p);
        }

        return exists;
    }

    T remove(int index) {
        // TODO: fix the remove to realign the index
        Player removedPlayer = lobby.remove(index);
        if (removedPlayer == null) {
            return null;
        }

        return playerControllersMap.remove(removedPlayer);
    }

    List<T> getPlayerControllers() {
        return lobby.get().stream().map(playerControllersMap::get).toList();
    }

    List<Player> getPlayers() {
        return lobby.get();
    }

    Lobby getLobby() {
        return lobby;
    }
}
