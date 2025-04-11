package parade.renderer.impl;

import parade.card.Card;
import parade.player.Player;
import parade.player.controller.PlayCardData;
import parade.renderer.ClientRenderer;
import parade.utils.Ansi;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A debug text-based implementation of the client renderer for development. Responsible for
 * displaying game state and prompting the user via console.
 */
public class DebugClientRenderer implements ClientRenderer {
    @Override
    public void render(String message) {
        System.out.print(message);
    }

    @Override
    public void renderln(String message) {
        System.out.println(message);
    }

    @Override
    public void renderf(String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public void renderWelcome() throws IllegalStateException {
        System.out.println(
                Ansi.PURPLE_BOLD
                        + "============================= Welcome to Parade!"
                        + " =============================="
                        + Ansi.RESET);
    }

    @Override
    public void renderMenu() {
        System.out.println("1. Start Game");
        System.out.println("2. Exit");
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderPlayersLobby(List<Player> players) {
        System.out.println("Players in lobby: ");
        for (int i = 1; i <= players.size(); i++) {
            System.out.printf("%d. %s%n", i, players.get(i - 1).getName());
        }
        System.out.println();
        System.out.println("1. Add Player" + (players.size() == 6 ? " (Lobby is full)" : ""));
        System.out.println("2. Add Computer" + (players.size() == 6 ? " (Lobby is full)" : ""));
        System.out.println(
                "3. Remove player/computer" + (players.isEmpty() ? " (Lobby is empty)" : ""));
        System.out.println("4. Start Game" + (players.size() < 2 ? " (Not enough players)" : ""));
        System.out.print("Please select an option: ");
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
