package parade.common.state.server;

import parade.player.IPlayer;

import java.io.Serial;
import java.util.Arrays;
import java.util.Map;

public class GameEndData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -9095972290671989555L;

    private final IPlayer[] winners;
    private final Map<IPlayer, Integer> playerScores;

    public GameEndData(IPlayer[] winners, Map<IPlayer, Integer> playerScores) {
        super(ServerAction.GAME_END);
        this.winners = Arrays.copyOf(winners, winners.length);
        this.playerScores = Map.copyOf(playerScores);
    }

    public IPlayer[] getWinners() {
        return winners;
    }

    public Map<IPlayer, Integer> getPlayerScores() {
        return playerScores;
    }
}
