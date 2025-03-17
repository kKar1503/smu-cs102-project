package parade.common.state.server;

import parade.common.Card;
import parade.common.Player;

import java.io.Serial;
import java.util.Arrays;

public class ServerPlayerReceivedParadeCardsData extends AbstractServerData {
    @Serial private static final long serialVersionUID = -3735207135654680206L;

    private final Player recipient;
    private final Card[] paradeCards;

    public ServerPlayerReceivedParadeCardsData(Player recipient, Card[] paradeCards) {
        super(ServerAction.PLAYER_RECEIVED_PARADE_CARDS);
        this.recipient = recipient;
        this.paradeCards = Arrays.copyOf(paradeCards, paradeCards.length);
    }

    public Player getRecipient() {
        return recipient;
    }

    public Card[] getParadeCards() {
        return paradeCards;
    }
}
