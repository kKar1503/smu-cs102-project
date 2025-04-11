package parade.player.controller;

import parade.card.Parade;

import java.util.List;

public class PlayCardData {
    private final List<AbstractPlayerController> otherPlayers;
    private final Parade parade;
    private final int deckSize;

    public PlayCardData(List<AbstractPlayerController> otherPlayers, Parade parade, int deckSize) {
        this.otherPlayers = otherPlayers;
        this.parade = parade;
        this.deckSize = deckSize;
    }

    public List<AbstractPlayerController> getOtherPlayers() {
        return otherPlayers;
    }

    public Parade getParade() {
        return parade;
    }

    public int getDeckSize() {
        return deckSize;
    }
}
