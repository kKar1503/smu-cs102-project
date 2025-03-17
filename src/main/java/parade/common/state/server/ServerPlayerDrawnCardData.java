package parade.common.state.server;

import parade.common.Card;

import java.io.Serial;

public class ServerPlayerDrawnCardData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -4076633650641288697L;

    private final Card card;
    private final int remainingDeckSize;

    public ServerPlayerDrawnCardData(Card card, int remainingDeckSize) {
        super(ServerAction.PLAYER_DRAWN_CARD);
        this.card = card;
        this.remainingDeckSize = remainingDeckSize;
    }

    public Card getCard() {
        return card;
    }

    public int getRemainingDeckSize() {
        return remainingDeckSize;
    }
}
