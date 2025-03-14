package parade.common.state.server;

import parade.common.Card;

import java.io.Serial;
import java.util.Arrays;

public class GameStartData extends AbstractServerData {
    @Serial private static final long serialVersionUID = 1964183020387224824L;

    private final Card[] cards;

    public GameStartData(Card[] cards) {
        super(ServerAction.GAME_START);
        this.cards = Arrays.copyOf(cards, cards.length);
    }

    public Card[] getCards() {
        return cards;
    }
}
