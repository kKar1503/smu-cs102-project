package parade.common.state.server;

import parade.common.Player;

import java.io.Serial;
import java.util.Arrays;
import java.util.Map;

public class ServerGameEndData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -9095972290671989555L;

    private final Player[] winners;
    private final Map<Player, Integer> playerScores;

    public ServerGameEndData(Player[] winners, Map<Player, Integer> playerScores) {
        super(ServerAction.GAME_END);
        this.winners = Arrays.copyOf(winners, winners.length);
        this.playerScores = Map.copyOf(playerScores);
    }

    public Player[] getWinners() {
        return winners;
    }

    public Map<Player, Integer> getPlayerScores() {
        return playerScores;
    }
}
