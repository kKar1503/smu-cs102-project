package parade.common.state.server;

import parade.common.Card;
import parade.player.IPlayer;

import java.io.Serial;

public class PlayerPlayedCardData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -4076633650641288697L;

    private final IPlayer player;
    private final Card card;

    public PlayerPlayedCardData(IPlayer player, Card card) {
        super(ServerAction.PLAYER_PLAYED_CARD);
        this.player = player;
        this.card = card;
    }

    public IPlayer getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }
}
