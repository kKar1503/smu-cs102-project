package parade.renderer.local.impl;

import parade.card.Card;
import parade.player.Player;
import parade.player.controller.PlayCardData;
import parade.renderer.local.IClientRenderer;
import parade.utils.ConsoleColors;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BasicLocalClientRenderer implements IClientRenderer {
    public BasicLocalClientRenderer() {}

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
                ConsoleColors.PURPLE_BOLD
                        + "============================= Welcome to Parade!"
                        + " =============================="
                        + ConsoleColors.RESET);
    }

    @Override
    public void renderMenu() {
        System.out.println("1. Start Game");
        System.out.println("2. Exit");
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderPlayersLobby(List<Player> lobby) {
        System.out.println("Players in lobby: ");
        for (int i = 1; i <= lobby.size(); i++) {
            System.out.printf("%d. %s\n", i, lobby.get(i - 1).getName());
        }
        System.out.println();
        System.out.println("1. Add Player" + (lobby.size() == 6 ? " (Lobby is full)" : ""));
        System.out.println("2. Add Computer" + (lobby.size() == 6 ? " (Lobby is full)" : ""));
        System.out.println(
                "3. Remove player/computer" + (lobby.isEmpty() ? " (Lobby is empty)" : ""));
        System.out.println("4. Start Game" + (lobby.size() >= 2 ? " (Not enough players)" : ""));
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
    public void renderPlayerTurn(Player player, Card newlyDrawnCard, PlayCardData playCardData) {
        // print player's name and drawn card
        System.out.println("\n" + player.getName() + "'s turn.");
        if (newlyDrawnCard != null) {
            System.out.println(
                    "You drew: ["
                            + newlyDrawnCard.getNumber()
                            + " "
                            + newlyDrawnCard.getColour()
                            + "]");
        }
        // print cards in parade
        System.out.println(
                "\nParade\n======================================================================");
        List<Card> parade = playCardData.getParade().getCards();
        for (Card card : parade) {
            System.out.print((parade.indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        // sort board and print
        List<Card> board = player.getBoard();
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        System.out.println(
                "\n\n"
                        + "Your board\n"
                        + "===========================================================================");
        for (Card card : board) {
            System.out.print(printCards(card) + " ");
        }
        // print player's hand
        System.out.println(
                "\n\n"
                        + "Your hand\n"
                        + "==========================================================================");
        for (Card card : player.getHand()) {
            System.out.print((player.getHand().indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        System.out.print("\n\nSelect a card to play:");
    }

    @Override
    public void renderEndGame(Map<Player, Integer> playerScores) {
        System.out.println("Game Over!");
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    @Override
    public void renderSinglePlayerEndGame(Player player, int score) {
        System.out.println("Game Over, " + player.getName() + "!");
        System.out.println("Your score: " + score);
    }

    @Override
    public void renderBye() {
        System.out.println("Bye bye buddy.");
    }

    public String printCards(Card card) {
        return "[" + card.getNumber() + " " + card.getColour() + "]";
    }
}
