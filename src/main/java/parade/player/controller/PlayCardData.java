package parade.player.controller;

import parade.card.Card;

import java.util.List;

public class PlayCardData {
    private final List<AbstractPlayerController> otherPlayers;
    private final List<Card> parade;
    private final int deckSize;

    public PlayCardData(
            List<AbstractPlayerController> otherPlayers, List<Card> parade, int deckSize) {
        this.otherPlayers = otherPlayers;
        this.parade = parade;
        this.deckSize = deckSize;
    }

    public List<AbstractPlayerController> getOtherPlayers() {
        return otherPlayers;
    }

    public List<Card> getParade() {
        return parade;
    }

    public int getDeckSize() {
        return deckSize;
    }
}
