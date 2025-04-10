package parade.renderer.impl;

import parade.common.Card;
import parade.engine.AbstractGameEngine;
import parade.player.IPlayer;
import parade.renderer.IClientRenderer;
import parade.utils.ConsoleColors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
    }

    @Override
    public void renderMenu() throws IllegalStateException {
        // the stream holding the file content
        InputStream inFromFile = getClass().getClassLoader().getResourceAsStream("RenderMenu.txt");
        if (inFromFile == null) {
            throw new IllegalStateException("RenderMenu.txt not found");
        }
        Scanner s = new Scanner(inFromFile).useDelimiter("\\Z");
        String paradeWelcome = s.hasNext() ? s.next() : "";

        if (paradeWelcome != null) {
            System.out.println(
                    ConsoleColors.PURPLE_BOLD
                            + "============================= Welcome to Parade!"
                            + " =============================="
                            + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + paradeWelcome + ConsoleColors.RESET);
        }
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

        renderCardList(" Your scoring board ", board);
        renderCardList(" Cards in your hand ", player.getHand());

        System.out.print("\n\nSelect a card to play:");
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
                        + "═".repeat(Math.max(0, contentWidth - rawLabel.trim().length()))
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

    @Override
    public void renderEndGame(Map<IPlayer, Integer> playerScores) { // renderEndGame
        try {
            for (int i = 0; i < 30; i++) {
                System.out.print("\033[H\033[2J");
                System.out.flush();

                System.out.println(
                        " ".repeat(i)
                                + " ██████╗  █████╗ ███╗   ███╗███████╗     ██████╗ ██╗  "
                                + " ██╗███████╗██████╗ ");
                System.out.println(
                        " ".repeat(i)
                                + "██╔════╝ ██╔══██╗████╗ ████║██╔════╝     ██╔══██╗██║  "
                                + " ██║██╔════╝██╔══██╗");
                System.out.println(
                        " ".repeat(i)
                                + "██║  ███╗███████║██╔████╔██║█████╗       ██║  ██║██║   ██║█████╗"
                                + "  ██████╔╝");
                System.out.println(
                        " ".repeat(i)
                                + "██║   ██║██╔══██║██║╚██╔╝██║██╔══╝       ██║  ██║██║   ██║██╔══╝"
                                + "  ██╔══██╗");
                System.out.println(
                        " ".repeat(i)
                                + "╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    "
                                + " ██████╔╝╚██████╔╝███████╗██║  ██║");
                System.out.println(
                        " ".repeat(i)
                                + " ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝  ╚═════╝"
                                + " ╚══════╝╚═╝  ╚═╝");

                Thread.sleep(100);
            }

            for (int i = 0; i < 6; i++) {
                System.out.print("\033[H\033[2J");

                System.out.println("\n\033[5m");
                System.out.println("      =============================================");
                System.out.println("      ||    ███████╗██╗███╗   ██╗ █████╗ ██╗     ||");
                System.out.println("      ||    ██╔════╝██║████╗  ██║██╔══██╗██║     ||");
                System.out.println("      ||    █████╗  ██║██╔██╗ ██║███████║██║     ||");
                System.out.println("      ||    ██╔══╝  ██║██║╚██╗██║██╔══██║██║     ||");
                System.out.println("      ||    ██║     ██║██║ ╚████║██║  ██║███████╗||");
                System.out.println("      ||    ╚═╝     ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝╚══════╝||");
                System.out.println("      =============================================");
                System.out.println("\033[0m");
            }

            // Table Header
            System.out.println("        ┌──────────────────┬───────────┐");
            System.out.println("        │     Player       │  Score    │");
            System.out.println("        ├──────────────────┼───────────┤");

            // Display Player Scores in Table Format
            for (Map.Entry<IPlayer, Integer> entry : playerScores.entrySet()) {
                System.out.printf(
                        "       │ %-16s │ %7d   │\n", entry.getKey().getName(), entry.getValue());
            }

            // Table Footer
            System.out.println("        └──────────────────┴───────────┘");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void renderRoll() {
        String[] block = {
            "╔══════════╗",
            "║          ║",
            "║ ROLLING  ║",
            "║ DICE :)  ║",
            "║          ║",
            "╚══════════╝"
        };

        System.out.println("Shaking block...\n");

        for (int i = 0; i < 15; i++) {
            int offset = (int) (Math.random() * 6); // random indent 0–5
            printBlockWithOffset(block, offset);

            try {
                Thread.sleep(100); // short delay for shaking effect
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            clearConsole();
        }
    }

    private void printBlockWithOffset(String[] block, int offset) {
        String space = " ".repeat(offset);
        for (String line : block) {
            System.out.println(space + line);
        }
    }

    // This just prints many new lines to "clear" the screen for shaking effect
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private String returnDice(int num) {
        String[] toPrint = {};

        String whiteBg = "\u001B[47m";
        String blackText = "\u001B[30m";
        String reset = "\u001B[0m";

        String[] dice1 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "║    o    ║" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };

        String[] dice2 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o       ║" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "║       o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };

        String[] dice3 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o       ║" + reset,
            whiteBg + blackText + "║    o    ║" + reset,
            whiteBg + blackText + "║       o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };

        String[] dice4 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };

        String[] dice5 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║    o    ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };

        String[] dice6 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };

        switch (num) {
            case 1:
                toPrint = dice1;
                break;
            case 2:
                toPrint = dice2;
                break;
            case 3:
                toPrint = dice3;
                break;
            case 4:
                toPrint = dice4;
                break;
            case 5:
                toPrint = dice5;
                break;
            case 6:
                toPrint = dice6;
                break;
        }

        return String.join("\n", Arrays.stream(toPrint).toArray(String[]::new));
    }

    @Override
    public void renderBye() {
        // Goodbye Message
        System.out.println("\nTHANK YOU FOR PLAYING! SEE YOU NEXT TIME!\n");
    }
}
