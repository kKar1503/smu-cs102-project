package parade.renderer.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import parade.common.Card;
import parade.engine.AbstractGameEngine;
import parade.player.IPlayer;
import parade.renderer.IClientRenderer;
import parade.utils.ConsoleColors;

/**
 * A basic text-based implementation of the client renderer for local gameplay. Responsible for
 * displaying game state and prompting the user via console.
 */
public class BasicLocalClientRenderer implements IClientRenderer {

    public BasicLocalClientRenderer() {}

    /** Renders a plain message without newline. */
    @Override
    public void render(String message) {
        System.out.print(message);
    }

    /** Renders a message followed by a newline. */
    @Override
    public void renderln(String message) {
        System.out.println(message);
    }

    /**
     * Renders a formatted message to the console, similar to printf.
     *
     * @param format the format string
     * @param args the arguments to format the string with
     */
    @Override
    public void renderf(String format, Object... args) {
        System.out.printf(format, args);
    }

    /**
     * Renders the welcome banner for the game. Throws an exception if file is missing.
     *
     * @throws IllegalStateException if the resource file cannot be found
     */
    @Override
    public void renderWelcome() throws IllegalStateException {
        System.out.println(
                ConsoleColors.PURPLE_BOLD
                        + "============================= ✨ Welcome to Parade! ✨"
                        + " =============================="
                        + ConsoleColors.RESET);
    }

    /**
     * Renders the main menu from an external file.
     *
     * @throws IllegalStateException if the RenderMenu.txt file cannot be found
     */
    @Override
    public void renderMenu() throws IllegalStateException {
        InputStream inFromFile = getClass().getClassLoader().getResourceAsStream("RenderMenu.txt");
        if (inFromFile == null) {
            throw new IllegalStateException("RenderMenu.txt not found");
        }

        Scanner scanner = new Scanner(inFromFile).useDelimiter("\\Z");
        String menuText = scanner.hasNext() ? scanner.next() : "";
        scanner.close();

        if (menuText != null) {
            System.out.println(
                    ConsoleColors.PURPLE_BOLD
                            + "============================= Welcome to Parade!"
                            + " =============================="
                            + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + menuText + ConsoleColors.RESET);
        }
    }

    /**
     * Displays the list of players in the lobby and available actions.
     *
     * @param players list of players currently in the lobby
     */
    @Override
    public void renderPlayersLobby(List<IPlayer> players) {
        System.out.println("Players in lobby: ");
        for (int i = 0; i < players.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, players.get(i).getName());
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

    /** Renders the computer difficulty selection menu. */
    @Override
    public void renderComputerDifficulty() {
        System.out.println("Choose computer player's difficulty");
        System.out.println("1. Easy");
        System.out.println("2. Normal");
        System.out.println("3. Hard");
    }

    /**
     * Displays the turn information for a player including hand, board, and parade.
     *
     * @param player the current player
     * @param newlyDrawnCard the most recent drawn card
     * @param parade the current parade lineup
     */
    @Override
    public void renderPlayerTurn(
            IPlayer player, Card newlyDrawnCard, List<Card> parade, boolean toDiscard) {
        System.out.println("\n" + player.getName() + "'s turn.");
        if (newlyDrawnCard != null) {
            System.out.println("You drew:\n" + rendersSingleCard(newlyDrawnCard));
        }

        renderCardList(" Parade (read left to right, top to bottom!) ", parade);

        List<Card> board = new ArrayList<>(player.getBoard());
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        renderCardList(" Your scoring board ", board);
        renderCardList(" Cards in your hand ", player.getHand());

        System.out.printf("\n\nSelect a card to %s:", toDiscard ? "discard" : "play");
    }

    /**
     * Formats a single card as a short text string.
     *
     * @param card the card to format
     * @return formatted string representation
     */
    public String printCards(Card card) {
        return "[" + card.getNumber() + " " + card.getColour() + "]";
    }

    /**
     * Renders a list of cards horizontally in a bordered box with optional line wrapping.
     *
     * @param label the section label
     * @param cards the list of cards to display
     */
    public void renderCardList(String label, List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            System.out.println("\n╔" + ConsoleColors.purple(label) + "═".repeat(40) + "╗");
            System.out.println("║ No cards to display." + " ".repeat(39) + "║");
            System.out.println("╚" + "═".repeat(60) + "╝");
            return;
        }

        final int cardWidth = 20;
        final int spacing = 1;
        final int maxWidth = 100;

        int cardsPerRow = Math.max(1, (maxWidth + spacing) / (cardWidth + spacing));
        int totalCards = cards.size();

        List<String[]> renderedCards = new ArrayList<>();
        for (Card card : cards) {
            renderedCards.add(rendersSingleCard(card).split("\n"));
        }

        int linesPerCard = renderedCards.get(0).length;
        int cardsInFirstRow = Math.min(cardsPerRow, totalCards);
        int contentWidth = cardsInFirstRow * cardWidth + (cardsInFirstRow - 1) * spacing;

        String topBorder =
                "╔"
                        + ConsoleColors.purple(label)
                        + "═".repeat(Math.max(0, contentWidth - label.trim().length()))
                        + "╗";
        String bottomBorder = "╚" + "═".repeat(contentWidth + 2) + "╝";

        System.out.println("\n" + topBorder);

        for (int start = 0; start < totalCards; start += cardsPerRow) {
            int end = Math.min(start + cardsPerRow, totalCards);
            for (int line = 0; line < linesPerCard; line++) {
                System.out.print("║ ");
                for (int j = start; j < end; j++) {
                    System.out.print(renderedCards.get(j)[line]);
                    if (j < end - 1) System.out.print(" ");
                }

                int actualCards = end - start;
                if (actualCards < cardsPerRow) {
                    int missing = cardsPerRow - actualCards;
                    int pad = missing * (cardWidth + spacing);
                    System.out.print(" ".repeat(pad));
                }

                System.out.println(" ║");
            }
        }

        System.out.println(bottomBorder);
    }

    /**
     * Colors a string with background based on the given color name.
     *
     * @param colour the name of the color
     * @param text the text to be colorized
     * @return colored string
     */
    public String colorPrinter(String colour, String text) {
        return switch (colour) {
            case "RED" -> ConsoleColors.redBackground(text);
            case "BLUE" -> ConsoleColors.brightBlueBackground(text);
            case "GREEN" -> ConsoleColors.brightGreenBackground(text);
            case "YELLOW" -> ConsoleColors.brightYellowBackground(text);
            case "PURPLE" -> ConsoleColors.purpleBackground(text);
            default -> ConsoleColors.blackBackground(text);
        };
    }

    /**
     * Returns the ASCII art representation of a single card.
     *
     * @param card the card to render
     * @return multi-line string representation
     */
    public String rendersSingleCard(Card card) {
        String colorCode = card.getColour().name();
        int width = 18;
        String numberStr = String.valueOf(card.getNumber());
        String colorName = card.getColour().name();
        int padding = (width - colorName.length()) / 2;
        String centeredColorName =
                " ".repeat(padding) + colorName + " ".repeat(width - colorName.length() - padding);

        String[] lines = {
            "┌" + "─".repeat(width) + "┐",
            String.format("│ %-2s%s│", numberStr, " ".repeat(width - 3)),
            String.format("│%s│", " ".repeat(width)),
            String.format("│%s│", centeredColorName),
            String.format("│%-" + width + "s│", "      /\\__/\\"),
            String.format("│%-" + width + "s│", "     | @ . @|"),
            String.format("│%-" + width + "s│", "      \\  -  /"),
            String.format("│%-" + width + "s│", "  ////|     |\\\\\\\\"),
            String.format("│%-" + width + "s│", "   ==\\|__|__|/=="),
            String.format("│%s│", " ".repeat(width)),
            String.format("│%s%2s │", " ".repeat(width - 3), numberStr),
            "└" + "─".repeat(width) + "┘"
        };

        return String.join(
                "\n",
                Arrays.stream(lines)
                        .map(line -> colorPrinter(colorCode, line))
                        .toArray(String[]::new));
    }

    /**
     * Renders the game ending screen with animation and final scores.
     *
     * @param playerScores final score map of all players
     */
    @Override
    public void renderEndGame(Map<IPlayer, Integer> playerScores) {
        try {
            for (int i = 0; i < 30; i++) {
                clearConsole();
                String asciiArt = 
                  "  ______ _____ _   _          _      \n"
                + " |  ____|_   _| \\ | |   /\\   | |     \n"
                + " | |__    | | |  \\| |  /  \\  | |     \n"
                + " |  __|   | | | . ` | / /\\ \\ | |     \n"
                + " | |     _| |_| |\\  |/ ____ \\| |____ \n"
                + " |_|    |_____|_| \\_/_/    \\_\\______|\n"
                + "                                      \n"
                + "                                      ";

            System.out.println(purple(asciiArt));
                Thread.sleep(100);
            }

            for (int i = 0; i < 6; i++) {
                clearConsole();
                String asciiArt = 
                  "  ______ _____ _   _          _      \n"
                + " |  ____|_   _| \\ | |   /\\   | |     \n"
                + " | |__    | | |  \\| |  /  \\  | |     \n"
                + " |  __|   | | | . ` | / /\\ \\ | |     \n"
                + " | |     _| |_| |\\  |/ ____ \\| |____ \n"
                + " |_|    |_____|_| \\_/_/    \\_\\______|\n"
                + "                                      \n"
                + "                                      ";

            System.out.println(purple(asciiArt));
            }

            int playerColWidth = 32;
            int scoreColWidth = 9;
            String header =
                    String.format(
                            "        ┌%s┐\n"
                                    + "        │ %-"
                                    + playerColWidth
                                    + "s │ %-"
                                    + scoreColWidth
                                    + "s │\n"
                                    + "        ├%s┤",
                            "─".repeat(playerColWidth + 2 + scoreColWidth + 3),
                            "Player",
                            "Score",
                            "─".repeat(playerColWidth + 2) + "┼" + "─".repeat(scoreColWidth + 2));
            System.out.println(header);

            for (Map.Entry<IPlayer, Integer> entry : playerScores.entrySet()) {
                System.out.printf(
                        "        │ %-" + playerColWidth + "s │ %" + scoreColWidth + "d │%n",
                        entry.getKey().getName(),
                        entry.getValue());
            }

            System.out.println(
                    "        └"
                            + "─".repeat(playerColWidth + 2)
                            + "┴"
                            + "─".repeat(scoreColWidth + 2)
                            + "┘");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Renders an animated dice-rolling block with shaking effect. This is purely visual and does
     * not determine any game outcome.
     */
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

        // Simulate shaking animation by redrawing the block with random offsets
        for (int i = 0; i < 15; i++) {
            int offset = (int) (Math.random() * 6); // random indent between 0–5
            printBlockWithOffset(block, offset);

            try {
                Thread.sleep(100); // brief pause between shakes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }

            clearConsole(); // Clear console to simulate motion
        }
    }

    /**
     * Prints each line of a text block with a specified left offset (indent).
     *
     * @param block the text block to print (as array of lines)
     * @param offset number of spaces to pad each line with
     */
    private void printBlockWithOffset(String[] block, int offset) {
        String space = " ".repeat(offset);
        for (String line : block) {
            System.out.println(space + line);
        }
    }

    /**
     * Clears the console screen using ANSI escape codes. This helps give the illusion of a dynamic
     * animation.
     */
    private void clearConsole() {
        System.out.print(ConsoleColors.CLEAR);
        System.out.flush();
    }

    /**
     * Returns a stylized ASCII art representation of a dice face.
     *
     * @param num the number on the dice face (1–6)
     * @return a string of the dice drawn using box-drawing characters and dots
     */
    private String returnDice(int num) {
        String[] toPrint = {};


        // Define each possible dice face
        String[] dice1 = {
            ConsoleColors.whiteBgBlackText("╔═════════╗"),
            ConsoleColors.whiteBgBlackText("║         ║"),
            ConsoleColors.whiteBgBlackText("║    o    ║"),
            ConsoleColors.whiteBgBlackText("║         ║"),
            ConsoleColors.whiteBgBlackText("╚═════════╝")
        };

        String[] dice2 = {
            ConsoleColors.whiteBgBlackText("╔═════════╗"),
            ConsoleColors.whiteBgBlackText("║ o       ║"),
            ConsoleColors.whiteBgBlackText("║         ║"),
            ConsoleColors.whiteBgBlackText("║       o ║"),
            ConsoleColors.whiteBgBlackText("╚═════════╝")
        };
        
        String[] dice3 = {
            ConsoleColors.whiteBgBlackText("╔═════════╗"),
            ConsoleColors.whiteBgBlackText("║ o       ║"),
            ConsoleColors.whiteBgBlackText("║    o    ║"),
            ConsoleColors.whiteBgBlackText("║       o ║"),
            ConsoleColors.whiteBgBlackText("╚═════════╝")
        };
        
        String[] dice4 = {
            ConsoleColors.whiteBgBlackText("╔═════════╗"),
            ConsoleColors.whiteBgBlackText("║ o     o ║"),
            ConsoleColors.whiteBgBlackText("║         ║"),
            ConsoleColors.whiteBgBlackText("║ o     o ║"),
            ConsoleColors.whiteBgBlackText("╚═════════╝")
        };
        
        String[] dice5 = {
            ConsoleColors.whiteBgBlackText("╔═════════╗"),
            ConsoleColors.whiteBgBlackText("║ o     o ║"),
            ConsoleColors.whiteBgBlackText("║    o    ║"),
            ConsoleColors.whiteBgBlackText("║ o     o ║"),
            ConsoleColors.whiteBgBlackText("╚═════════╝")
        };
        
        String[] dice6 = {
            ConsoleColors.whiteBgBlackText("╔═════════╗"),
            ConsoleColors.whiteBgBlackText("║ o     o ║"),
            ConsoleColors.whiteBgBlackText("║ o     o ║"),
            ConsoleColors.whiteBgBlackText("║ o     o ║"),
            ConsoleColors.whiteBgBlackText("╚═════════╝")
        };

        // Choose the appropriate dice face
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

    /** Renders a simple farewell message at the end of the game session. */
    @Override
    public void renderBye() {
        System.out.println("\nTHANK YOU FOR PLAYING! SEE YOU NEXT TIME!\n");
    }
}
