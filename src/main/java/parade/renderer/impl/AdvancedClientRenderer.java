package parade.renderer.impl;

import parade.common.Card;
import parade.common.Colour;
import parade.engine.AbstractGameEngine;
import parade.player.IPlayer;
import parade.renderer.IClientRenderer;
import parade.utils.ConsoleColors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdvancedClientRenderer implements IClientRenderer {
    public AdvancedClientRenderer() {}

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
        // the stream holding the file content
        InputStream inFromFile =
                getClass().getClassLoader().getResourceAsStream("parade_ascii_art.txt");
        if (inFromFile == null) {
            throw new IllegalStateException("parade_ascii_art.txt not found");
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
            System.out.println(
                    "===================================================================================");

            System.out.println(renderSingleCard(new Card(1, Colour.BLACK), 4));
        }
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
                "2. Start Game"
                        + (players.size() < AbstractGameEngine.MIN_PLAYERS
                                ? " (Not enough players)"
                                : ""));
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderComputerDifficulty() {}
    ;

    @Override
    public void renderPlayerTurn(IPlayer player, Card newlyDrawnCard, List<Card> parade) {
        // print player's name and drawn card
        System.out.println("\n" + player.getName() + "'s turn.");
        if (newlyDrawnCard != null) {
            System.out.println("You drew:" + renderSingleCard(newlyDrawnCard, 4));
        }
        // print cards in parade
        System.out.println(
                "\nParade\n======================================================================");
        printCardsHorizontally(parade, false);
        // sort board and print
        List<Card> board = player.getBoard();
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        System.out.println(
                "\n\n"
                    + "Your board\n"
                    + "===========================================================================");

        printStackedCards(board);

        // print player's hand
        System.out.println(
                "\n\n"
                    + "Your hand\n"
                    + "==========================================================================");
        printCardsHorizontally(parade, true);
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
    public void renderBye() {
        // Goodbye Message
        System.out.println("\nTHANK YOU FOR PLAYING! SEE YOU NEXT TIME!\n");
    }

    /**
     * Render the parade or the player hand, depending on the boolean set
     *
     * @param board List of cards to take in, depending on use, is either the parade, or the
     *     player's hand
     * @param options True - enables indices printing. False - prints parade only
     */
    public void printCardsHorizontally(List<Card> board, boolean options) {
        int padding = 3;
        int width = 5;

        StringBuilder sbIndices = new StringBuilder();

        // To store the card parts (top, middle, and bottom)
        StringBuilder sbTop = new StringBuilder();
        StringBuilder sbMiddle = new StringBuilder();
        StringBuilder sbBottom = new StringBuilder();

        // For each card in the board, generate the index and the card's parts
        for (int i = 0; i < board.size(); i++) {

            Card card = board.get(i);

            if (options == true) {
                sbIndices.append(" ".repeat(padding + (width / 2)));
                sbIndices.append(
                        printConsoleColour(
                                String.valueOf(card.getColour()).toLowerCase(),
                                String.format("[%d]", i + 1)));

                sbIndices.append(" ".repeat(2)); // Add index above each card
            }

            // Render the top half, middle, and bottom parts of the card
            sbTop.append(renderTopHalfCard(card, padding)).append(" ");
            sbMiddle.append(renderMiddleCard(card, padding)).append(" ");
            sbBottom.append(renderBottomHalfCard(card, padding)).append(" ");
        }

        // Print the indices row above the cards
        if (options == true) {
            System.out.println(sbIndices);
        }

        // Print the card rows (top, middle, and bottom)
        System.out.println(sbTop);
        System.out.println(sbMiddle);
        System.out.println(sbBottom);
    }

    /**
     * Renders the player's scoring zone
     *
     * @param board List of cards to take in to render the player's scoring zone.
     */
    public void printStackedCards(List<Card> board) {
        int padding = 3;
        int width = 5;
        board = new ArrayList<Card>(board);
        board.sort(Comparator.comparing(card -> card.getColour()));

        Map<Colour, List<Card>> colourCardMap = new LinkedHashMap<>();

        // finds the last card to display
        for (Card w : board) {
            colourCardMap.computeIfAbsent(w.getColour(), k -> new ArrayList<>()).add(w);
        }

        int max = 0;

        for (Colour c1 : colourCardMap.keySet()) {
            int numRows = colourCardMap.get(c1).size();
            if (numRows > max) {
                max = numRows;
            }
        }

        for (int i = 0; i < max + 2; i++) { // i refers to row number
            StringBuffer sb = new StringBuffer();

            for (Colour c : colourCardMap.keySet()) {
                List<Card> cardList = colourCardMap.get(c);

                // check if have any cards for this row.
                int colourCount = colourCardMap.get(c).size() - 1; // indexs

                if (colourCount >= i) {
                    // Not the last card, render top half
                    sb.append(renderTopHalfCard(cardList.get(i), padding));

                } else {
                    if (colourCount < i && colourCount + 1 == i) {
                        // This is the last card for this color, render bottom half
                        sb.append(renderMiddleCard(cardList.get(colourCount), padding));
                    } else if (colourCount < i && colourCount + 2 == i) {
                        sb.append(renderBottomHalfCard(cardList.get(colourCount), padding));
                    } else {
                        // No card for this row and color, add empty space
                        // Calculate width of a card with padding

                        sb.append(" ".repeat(width));
                        sb.append(" ".repeat(padding + 1)); // add one due to the characters
                    }
                }
            }

            System.out.println(sb);
        }
    }

    /**
     * Renders a single card
     *
     * @param card Card to be rendered
     * @param gaps Left-Right Padding surrounding the card
     */
    public String renderSingleCard(Card card, int gaps) {
        return "\n"
                + renderTopHalfCard(card, gaps)
                + "\n"
                + renderMiddleCard(card, gaps)
                + "\n"
                + renderBottomHalfCard(card, gaps);
    }

    /**
     * Renders the top portion of the card
     *
     * @param card Card to be rendered
     * @param gaps Left-Right Padding surrounding the card
     */
    public String renderTopHalfCard(Card card, int gaps) {
        String cardColour = "" + card.getColour();
        String lowerCardColour = cardColour.toLowerCase();

        int width = 5; // t.getWidth() * 0.2;

        // Top border with rounded corners
        String halfCard = (" ".repeat(gaps) + "╭" + card.getNumber() + "─".repeat(width - 2) + "╮");

        return printConsoleColour(lowerCardColour, halfCard); // should print error?
    }

    /**
     * Renders the middle portion of the card
     *
     * @param card Card to be rendered
     * @param gaps Left-Right Padding surrounding the card
     */
    public String renderMiddleCard(Card card, int gaps) {
        int width = 5;
        String cardColour = "" + card.getColour();
        String lowerCardColour = cardColour.toLowerCase();
        String emoji = null;

        switch (card.getColour()) {
            case Colour.BLACK:
                emoji = "🐇"; // rabbit
                break;
            case Colour.BLUE:
                emoji = "🧍‍♀️"; // alice
                break;
            case Colour.GREEN:
                emoji = "🥚"; // egg
                break;
            case Colour.RED:
                emoji = "🎩"; // mad hatter
                break;

            case Colour.YELLOW:
                emoji = "🦤"; // dodo
                break;
            case Colour.PURPLE:
                emoji = "🐈"; // cat
                break;
        }

        String middleCard = " ".repeat(gaps) + "|" + " ".repeat(1) + emoji + " ".repeat(1) + "|";

        return printConsoleColour(lowerCardColour, middleCard); // should print error?
    }

    /**
     * Renders the bottom portion of the card
     *
     * @param card Card to be rendered
     * @param gaps Left-Right Padding surrounding the card
     */
    public String renderBottomHalfCard(Card card, int gaps) {
        String cardColour = "" + card.getColour();
        String lowerCardColour = cardColour.toLowerCase();

        int width = 5; // t.getWidth() * 0.2;

        // Top border with rounded corners
        String halfCard = " ".repeat(gaps) + "╰" + "─".repeat(width - 1) + "╯";

        return printConsoleColour(lowerCardColour, halfCard);
    }

    /**
     * Helper function to print a String into a certain colour
     *
     * @param colour Colour to turn the String into
     * @param colourisedString String to turn into a the colour set
     */
    public String printConsoleColour(String colour, String colourisedString) {
        switch (colour) {
            case "red":
                return ConsoleColors.red(colourisedString);
            case "black":
                return ConsoleColors.black(colourisedString);
            case "green":
                return ConsoleColors.green(colourisedString);
            case "blue":
                return ConsoleColors.blue(colourisedString);
            case "yellow":
                return ConsoleColors.yellow(colourisedString);
            case "purple":
                return ConsoleColors.purple(colourisedString);
            default:
                return colourisedString; // no colour - white
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
}
