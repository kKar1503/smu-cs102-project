package parade.renderer.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import parade.common.Card;
import parade.common.Colour;
import parade.engine.AbstractGameEngine;
import parade.player.IPlayer;
import parade.renderer.IClientRenderer;
import parade.utils.ConsoleColors;

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

        List<Card> carList = new ArrayList<>();
        carList.add(new Card(0, Colour.RED));
        carList.add(new Card(2, Colour.RED));
        carList.add(new Card(1, Colour.RED));
        carList.add(new Card(4, Colour.RED));
        renderParade(carList);
        // System.out.println(rendersSingleCard(new Card(0, Colour.RED)));

        // System.out.println(rendersSingleCard(new Card(0, Colour.PURPLE)));
        // System.out.println(rendersSingleCard(new Card(0, Colour.YELLOW)));
        // System.out.println(rendersSingleCard(new Card(0, Colour.BLUE)));
        // System.out.println(rendersSingleCard(new Card(0, Colour.BLACK)));

        // System.out.println(rendersSingleCard(new Card(0, Colour.GREEN)));
    }

    @Override
    public void renderMenu() {
        System.out.println("1. Start Game");
        System.out.println("2. Exit");
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderPlayersLobby(List<IPlayer> players) {
        System.out.println("Players in lobby: ");
        for (int i = 1; i <= players.size(); i++) {
            System.out.printf("%d. %s\n", i, players.get(i - 1).getName());
        }
        System.out.println();
        System.out.println(
                "1. Add Player"
                        + (players.size() == AbstractGameEngine.MAX_PLAYERS
                                ? " (Lobby is full)"
                                : ""));
        System.out.println(
                "2. Add Computer"
                        + (players.size() == AbstractGameEngine.MAX_PLAYERS
                                ? " (Lobby is full)"
                                : ""));
        System.out.println(
                "3. Remove player/computer" + (players.isEmpty() ? " (Lobby is empty)" : ""));
        System.out.println(
                "4. Start Game"
                        + (players.size() < AbstractGameEngine.MIN_PLAYERS
                                ? " (Not enough players)"
                                : ""));
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
    public void renderPlayerTurn(IPlayer player, Card newlyDrawnCard, List<Card> parade) {
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
    public void renderEndGame(Map<IPlayer, Integer> playerScores) {
        System.out.println("Game Over!");
        for (Map.Entry<IPlayer, Integer> entry : playerScores.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    @Override
    public void renderSinglePlayerEndGame(IPlayer player, int score) {
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

    public void renderParade(List<Card> parade) {
        int padding = 6;
        int width = parade.size() * 18 + padding; // 6 for padding

        // Create the border
        String top_border = "╔" + ConsoleColors.purple(" Parade ") + "══".repeat(width) + "╗";

        String bottom_border = "╚" + "══".repeat(width + 4) + "╝";

        System.out.println(top_border);

        System.out.print("║ ");

        for (Card card : parade) {
            System.out.print((parade.indexOf(card) + 1) + "." + rendersSingleCard(card) + " ");
        }
        System.out.println(" ║");
        System.out.println(bottom_border);
    }

    public String colorPrinter(String colour, String text) {
        String colorCode = null;

        switch (colour) {
            case "RED":
                colorCode = ConsoleColors.redBackground(text);
                break;
            case "BLUE":
                colorCode = ConsoleColors.brightBlueBackground(text);
                break;
            case "GREEN":
                colorCode = ConsoleColors.brightGreenBackground(text);
                break;
            case "YELLOW":
                colorCode = ConsoleColors.brightYellowBackground(text);
                break;
            case "PURPLE":
                colorCode = ConsoleColors.purpleBackground(text);
                break;
            default:
                colorCode = ConsoleColors.blackBackground(text);
                break;
        }

        return colorCode;
    }

    public String rendersSingleCard(Card card) {
        String colorCode = String.valueOf(card.getColour());

        String numberStr = String.valueOf(card.getNumber());
        String colorName = "" + card.getColour();
        int width = 18;

        String top = "┌" + "─".repeat(width) + "┐";
        String bottom = "└" + "─".repeat(width) + "┘";
        String line1 = String.format("│ %-2s%s│", numberStr, " ".repeat(width - 3));
        String line2 = String.format("│%s│", " ".repeat(width));
        String line3 =
                String.format(
                        "│%"
                                + ((width + colorName.length()) / 2)
                                + "s%"
                                + ((width - colorName.length()) / 2)
                                + "s │",
                        colorName,
                        "");
        String line4 = line2;
        String line5 = String.format("│%s%2s │", " ".repeat(width - 3), numberStr);
        String pikachu1 = String.format("│%-18s│", "      /\\__/\\");
        String pikachu2 = String.format("│%-18s│", "     | @ . @|");
        String pikachu3 = String.format("│%-18s│", "      \\  -  /");
        String pikachu4 = String.format("│%-18s│", "  ////|     |\\\\\\\\");
        String pikachu5 = String.format("│%-18s│", "   ==\\|__|__|/==");

        return String.join(
                "\n",
                colorPrinter(colorCode, top),
                colorPrinter(colorCode, line1),
                colorPrinter(colorCode, line2),
                colorPrinter(colorCode, line3),
                colorPrinter(colorCode, pikachu1),
                colorPrinter(colorCode, pikachu2),
                colorPrinter(colorCode, pikachu3),
                colorPrinter(colorCode, pikachu4),
                colorPrinter(colorCode, pikachu5),
                colorPrinter(colorCode, line4),
                colorPrinter(colorCode, line5),
                colorPrinter(colorCode, bottom));
    }
}
