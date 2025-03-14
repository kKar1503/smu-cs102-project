package parade.common.state.server;

import parade.common.Card;
import parade.player.IPlayer;

import java.io.Serial;
import java.util.Arrays;

public class PlayerTurnData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -4944069289012740768L;

    private final IPlayer[] players;
    private final IPlayer currentTurnPlayer;
    private final Card[] parade;
    private final int deckSize;
    private final int playerHandSize;

    public PlayerTurnData(
            IPlayer[] players,
            IPlayer currentTurnPlayer,
            Card[] parade,
            int deckSize,
            int playerHandSize) {
        super(ServerAction.PLAYER_TURN);
        this.players = Arrays.copyOf(players, players.length);
        this.currentTurnPlayer = currentTurnPlayer;
        this.parade = Arrays.copyOf(parade, parade.length);
        this.deckSize = deckSize;
        this.playerHandSize = playerHandSize;
    }

    public IPlayer[] getPlayers() {
        return players;
    }

    public IPlayer getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public Card[] getParade() {
        return parade;
    }

    public int getDeckSize() {
        return deckSize;
    }

    /**
     * Get the size of the player's hand. This is mainly for the network transmission to tell other
     * players what's the size of the current player's hand
     *
     * @return the size of the player's hand
     */
    public int getPlayerHandSize() {
        return playerHandSize;
    }
}
