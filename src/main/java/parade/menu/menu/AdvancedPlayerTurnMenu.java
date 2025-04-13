package parade.menu.menu;

import parade.exception.MenuCancelledException;
import parade.menu.base.AbstractMenu;
import parade.menu.display.HorizontalCardsDisplay;
import parade.menu.display.StackedCardsDisplay;
import parade.menu.prompt.NumericPrompt;
import parade.player.Player;
import parade.player.controller.PlayCardData;

public class AdvancedPlayerTurnMenu extends AbstractMenu<Integer> {
    private final Player player;
    private final PlayCardData playCardData;
    private final boolean toDiscard;

    public AdvancedPlayerTurnMenu(Player player, PlayCardData playCardData, boolean toDiscard) {
        this.player = player;
        this.playCardData = playCardData;
        this.toDiscard = toDiscard;
    }

    @Override
    public Integer start() throws MenuCancelledException {
        println(NEW_LINE + player.getName() + "'s turn.");

        // Render Parade (stacked)
        println(NEW_LINE + "Parade");
        new HorizontalCardsDisplay(playCardData.getParade().getCards(), false).display();

        // Render Scoring Board (stacked)
        println(NEW_LINE + "Your board");
        new StackedCardsDisplay(player.getBoard()).display();

        // Render Hand (horizontal with selection)
        println(System.lineSeparator() + "Your hand");
        new HorizontalCardsDisplay(player.getHand(), true).display();

        printf("%n%nSelect a card to %s:", toDiscard ? "discard" : "play");
        flush();

        return new NumericPrompt(player.getHand().size()).prompt();
    }
}
