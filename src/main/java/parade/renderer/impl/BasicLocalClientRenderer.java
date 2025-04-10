package parade.renderer.impl;

import parade.common.Card;
import parade.engine.AbstractGameEngine;
import parade.player.IPlayer;
import parade.renderer.IClientRenderer;
import parade.utils.ConsoleColors;

import java.util.ArrayList;
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
                        + "============================= ✨ Welcome to Parade! ✨"
                        + " =============================="
                        + ConsoleColors.RESET);

        // List<Card> testParade = new ArrayList<>();
        // for (int i = 0; i < 12; i++) {
        //     testParade.add(new Card(i % 6, Colour.values()[i % Colour.values().length]));
        // }
        // renderCardList(" Parade (read left to right, top to bottom!) ", testParade);
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
            System.out.println("You drew:\n" + rendersSingleCard(newlyDrawnCard));
        }
        // print cards in parade
        renderCardList(" Parade (read left to right, top to bottom!) ", parade);

        // sort board and print
        List<Card> board = player.getBoard();
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        // System.out.println(
        //         "\n\n"
        //             + "Your board\n"
        //             +
        // "===========================================================================");
        // for (Card card : board) {
        //     System.out.print(printCards(card) + " ");
        // }
        renderCardList(" Your scoring board ", board);
        // print player's hand
        // System.out.println(
        //         "\n\n"
        //             + "Your hand\n"
        //             +
        // "==========================================================================");
        // for (Card card : player.getHand()) {
        //     System.out.print((player.getHand().indexOf(card) + 1) + "." + printCards(card) + "
        // ");
        // }
        renderCardList(" Cards in your hand ", player.getHand());

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

    public void renderCardList(String rawLabel, List<Card> parade) {
        if (parade == null || parade.isEmpty()) {
            System.out.println("\n╔" + ConsoleColors.purple(rawLabel) + "═".repeat(40) + "╗");
            System.out.println("║ No cards to display." + " ".repeat(39) + "║");
            System.out.println("╚" + "═".repeat(60) + "╝");
            return;
        }

        int cardWidth = 20;
        int spacing = 1;
        int maxTerminalWidth = 100;

        int cardsPerRow = Math.max(1, (maxTerminalWidth + spacing) / (cardWidth + spacing));
        int totalCards = parade.size();

        // Step 1: Render cards to line arrays
        List<String[]> renderedCards = new ArrayList<>();
        for (Card card : parade) {
            renderedCards.add(rendersSingleCard(card).split("\n"));
        }

        int numLinesPerCard = renderedCards.get(0).length;
        int cardsInFirstRow = Math.min(cardsPerRow, totalCards);
        int contentWidth = cardsInFirstRow * cardWidth + (cardsInFirstRow - 1) * spacing;

        // Step 2: Create the single top border
        String coloredLabel = ConsoleColors.purple(rawLabel);
        String topBorder =
                "╔"
                        + coloredLabel
                        + "═".repeat(contentWidth - rawLabel.trim().length())
                        + "╗"; //  + 3
        String bottomBorder = "╚" + "═".repeat(contentWidth + 2) + "╝";

        // Step 3: Print the top border once
        System.out.println("\n" + topBorder);

        // Step 4: Print all card rows line by line
        for (int start = 0; start < totalCards; start += cardsPerRow) {
            int end = Math.min(start + cardsPerRow, totalCards);

            for (int line = 0; line < numLinesPerCard; line++) {
                System.out.print("║ ");
                for (int j = start; j < end; j++) {
                    System.out.print(renderedCards.get(j)[line]);
                    if (j < end - 1) System.out.print(" ");
                }

                // pad right if this row is shorter than cardsPerRow
                int actualCards = end - start;
                if (actualCards < cardsPerRow) {
                    int missing = cardsPerRow - actualCards;
                    int pad = missing * (cardWidth + spacing);
                    System.out.print(" ".repeat(pad));
                }

                System.out.println(" ║");
            }
        }

        // Step 5: Print bottom border once
        System.out.println(bottomBorder);
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
        String colorCode = card.getColour().name(); // Use enum name() for consistency

        String numberStr = String.valueOf(card.getNumber());
        String colorName = card.getColour().name();
        int width = 18;

        // Center the color name properly
        int padding = (width - colorName.length()) / 2;
        String centeredColorName =
                " ".repeat(padding) + colorName + " ".repeat(width - colorName.length() - padding);

        String top = "┌" + "─".repeat(width) + "┐";
        String bottom = "└" + "─".repeat(width) + "┘";
        String line1 =
                String.format("│ %-2s%s│", numberStr, " ".repeat(width - 3)); // top-left number
        String line2 = String.format("│%s│", " ".repeat(width)); // empty line
        String line3 = String.format("│%s│", centeredColorName); // centered color name
        String line4 = line2;
        String line5 =
                String.format("│%s%2s │", " ".repeat(width - 3), numberStr); // bottom-right number

        // Pikachu ASCII art
        String pikachu1 = String.format("│%-" + width + "s│", "      /\\__/\\");
        String pikachu2 = String.format("│%-" + width + "s│", "     | @ . @|");
        String pikachu3 = String.format("│%-" + width + "s│", "      \\  -  /");
        String pikachu4 = String.format("│%-" + width + "s│", "  ////|     |\\\\\\\\");
        String pikachu5 = String.format("│%-" + width + "s│", "   ==\\|__|__|/==");

        // Return card string with color applied line-by-line
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
