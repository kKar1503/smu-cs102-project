package parade.menu.menu;

import parade.card.Card;
import parade.exceptions.MenuCancelledException;
import parade.menu.base.AbstractMenu;
import parade.menu.prompt.OptionsPrompt;
import parade.player.Player;
import parade.player.controller.PlayCardData;

import java.util.*;

public class DebugPlayerTurnMenu extends AbstractMenu<Integer> {
    private final Player player;
    private final PlayCardData playCardData;
    private final boolean toDiscard;

    public DebugPlayerTurnMenu(Player player, PlayCardData playCardData, boolean toDiscard) {
        this.player = player;
        this.playCardData = playCardData;
        this.toDiscard = toDiscard;
    }

    @Override
    public Integer start() throws MenuCancelledException {
        // print player's name and drawn card
        println();
        println(player.getName() + "'s turn.");
        // print cards in parade
        println();
        println("Parade");
        println("======================================================================");
        List<Card> paradeCards = playCardData.getParade().getCards();
        for (Card card : paradeCards) {
            print((paradeCards.indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        // sort board and print
        List<Card> board = player.getBoard();
        board = new ArrayList<>(board);
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        println();
        println();
        println("Your board");
        println("===========================================================================");
        for (Card card : board) {
            print(printCards(card) + " ");
        }
        // print player's hand
        println();
        println();
        println("Your hand");
        println("==========================================================================");
        for (Card card : player.getHand()) {
            print((player.getHand().indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        printf("%n%nSelect a card to %s:%n", toDiscard ? "discard" : "play");
        flush();
        return new OptionsPrompt(
                        player.getHand().stream().map(this::printCards).toArray(String[]::new))
                .prompt();
    }

    public String printCards(Card card) {
        return "[" + card.getNumber() + " " + card.getColour() + "]";
    }
}
