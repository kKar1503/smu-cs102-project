package parade.common.state.client;

import parade.common.Card;
import parade.common.Player;

import java.io.Serial;

public class PlayerCardPlayData extends AbstractClientData {
    @Serial private static final long serialVersionUID = 6422976455186594230L;

    private final Card card;

    public PlayerCardPlayData(Player caller, Card card) {
        super(caller, ClientAction.CARD_PLAY);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
