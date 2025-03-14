package parade.common.state.server;

import parade.common.Card;
import parade.player.IPlayer;

import java.io.Serial;
import java.util.Arrays;

public class PlayerReceivedParadeCardsData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -3735207135654680206L;

    private final IPlayer recipient;
    private final Card[] paradeCards;

    public PlayerReceivedParadeCardsData(IPlayer recipient, Card[] paradeCards) {
        super(ServerAction.PLAYER_RECEIVED_PARADE_CARDS);
        this.recipient = recipient;
        this.paradeCards = Arrays.copyOf(paradeCards, paradeCards.length);
    }

    public IPlayer getRecipient() {
        return recipient;
    }

    public Card[] getParadeCards() {
        return paradeCards;
    }
}
