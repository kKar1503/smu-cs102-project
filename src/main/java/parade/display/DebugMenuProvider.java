package parade.display;

import parade.card.Card;
import parade.display.option.LobbyMenuOption;
import parade.display.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

/**
 * A debug text-based implementation of the client renderer for development. Responsible for
 * displaying game state and prompting the user via console.
 */
public class DebugMenuProvider implements MenuProvider {
    @Override
    public void renderWelcome() throws IllegalStateException {
        new DynamicSeparator("Welcome to Parade!", Ansi.PURPLE::apply).display();
    }

    @Override
    public MainMenuOption mainMenuPrompt() {
        return new MainMenu().prompt();
    }

    @Override
    public LobbyMenuOption renderPlayersLobby(List<Player> players) {
        return new LobbyMenu(players).prompt();
    }

    @Override
    public void renderComputerDifficulty() {
        System.out.println("Choose computer player's difficulty");
        System.out.println("1. Easy");
        System.out.println("2. Normal");
        System.out.println("3. Hard");
    }

    @Override
    public void renderPlayerTurn(
            Player player, Card newlyDrawnCard, PlayCardData playCardData, boolean toDiscard) {
        // print player's name and drawn card
        System.out.println(System.lineSeparator() + player.getName() + "'s turn.");
        if (newlyDrawnCard != null) {
            System.out.println(
                    "You drew: ["
                            + newlyDrawnCard.getNumber()
                            + " "
                            + newlyDrawnCard.getColour()
                            + "]");
        }
        // print cards in parade
        System.out.println();
        System.out.println(
                "Parade"
                        + System.lineSeparator()
                        + "======================================================================");
        List<Card> paradeCards = playCardData.getParade().getCards();
        for (Card card : paradeCards) {
            System.out.print((paradeCards.indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        // sort board and print
        List<Card> board = player.getBoard();
        board = new ArrayList<>(board);
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        System.out.println();
        System.out.println();
        System.out.println(
                "Your board"
                        + System.lineSeparator()
                        + "===========================================================================");
        for (Card card : board) {
            System.out.print(printCards(card) + " ");
        }
        // print player's hand
        System.out.println();
        System.out.println();
        System.out.println(
                "Your hand"
                        + System.lineSeparator()
                        + "==========================================================================");
        for (Card card : player.getHand()) {
            System.out.print((player.getHand().indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        System.out.printf("%n%nSelect a card to %s:", toDiscard ? "discard" : "play");
    }

    @Override
    public void renderEndGame(Map<Player, Integer> playerScores) {
        System.out.println("Game Over!");
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    @Override
    public void renderBye() {
        System.out.println("Bye bye buddy.");
    }

    public String printCards(Card card) {
        return "[" + card.getNumber() + " " + card.getColour() + "]";
    }
}
