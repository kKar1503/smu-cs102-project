package parade.common.state.server;

import parade.common.Card;
import parade.common.Player;

import java.io.Serial;

public class PlayerPlayedCardData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -4076633650641288697L;

    private final Player player;
    private final Card card;

    public PlayerPlayedCardData(Player player, Card card) {
        super(ServerAction.PLAYER_PLAYED_CARD);
        this.player = player;
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }
}
