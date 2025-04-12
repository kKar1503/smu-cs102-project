package parade.menu.manager;

import parade.card.Card;
import parade.card.Colour;
import parade.menu.display.AsciiWelcome;
import parade.menu.menu.*;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

/**
 * AdvancedClientRenderer provides advanced rendering capabilities for the Parade game. It outputs
 * styled game content to the console.
 */
public class AdvancedMenuManager extends AbstractMenuManager {
    /**
     * Renders a stylized welcome message from an ASCII art file. Throws an exception if the file
     * cannot be found. Also displays a sample Parade card.
     */
    @Override
    public void welcomeDisplay() throws IllegalStateException {
        new AsciiWelcome().display();
    }

    /**
     * Displays the current state of a player's turn, including: - Drawn card (if any) - Parade line
     * - Player's board (sorted) - Player's hand Prompts the player to select a card to play.
     *
     * @param player The player whose turn is being rendered.
     * @param newlyDrawnCard The card the player drew this turn.
     * @param playCardData The data object containing information about the game state.
     */
    @Override
    public void renderPlayerTurn(
            Player player, Card newlyDrawnCard, PlayCardData playCardData, boolean toDiscard) {

        System.out.println(System.lineSeparator() + player.getName() + "'s turn.");

        // Show newly drawn card
        if (newlyDrawnCard != null) {
            System.out.println("You drew:" + renderSingleCard(newlyDrawnCard, 4));
        }

        // Render Parade (stacked)
        System.out.println(System.lineSeparator() + "Parade");
        printCardsHorizontally(playCardData.getParade().getCards(), false);

        // Render Scoring Board (stacked)
        System.out.println(System.lineSeparator() + "Your board");
        printStackedCards(player.getBoard());

        // Render Hand (horizontal with selection)
        System.out.println(System.lineSeparator() + "Your hand");
        printCardsHorizontally(player.getHand(), true);

        System.out.printf("%n%nSelect a card to %s:", toDiscard ? "discard" : "play");
    }

    /** Displays a farewell message when the game ends. */
    @Override
    public void renderBye() {
        System.out.println(System.lineSeparator() + "THANK YOU FOR PLAYING! SEE YOU NEXT TIME!");
    }

    /**
     * Renders a row of cards horizontally with a surrounding box. This is used primarily for
     * displaying cards in hand, where selection is needed. Cards are rendered with consistent
     * spacing, and optionally with indexed labels for user input.
     *
     * @param board List of cards to render (either the parade or player's hand)
     * @param showIndices If true, index labels are shown above cards (used for hand selection)
     */
    public void printCardsHorizontally(List<Card> board, boolean showIndices) {

        int padding = 3;
        int width = 5; // card width
        int leftIndexPadding = (padding + width / 2) - 1;
        int totalCardWidth = padding + width + 2;
        int rightIndexPadding;

        String verticalBorder = "║";
        String rightTopCornerBorder = "╗";
        String leftTopCornerBorder = "╔";
        String rightBottomCornerBorder = "╝";
        String leftBottomCornerBorder = "╚";
        String horizontalBorder = "═";
        String emptySpace = " ";
        String emptyBoxDisplay = "No cards to display.";
        String index;

        // StringBuilders for different card parts
        StringBuilder sbIndices = new StringBuilder();
        StringBuilder sbTop = new StringBuilder();
        StringBuilder sbMiddle = new StringBuilder();
        StringBuilder sbBottom = new StringBuilder();
        StringBuilder sbEmptyBox = new StringBuilder();

        if (board == null || board.isEmpty()) {
            // if no board, should return error!
            sbEmptyBox.append(
                    leftTopCornerBorder
                            + horizontalBorder.repeat(padding)
                            + horizontalBorder.repeat(emptyBoxDisplay.length())
                            + horizontalBorder.repeat(padding)
                            + rightTopCornerBorder
                            + "\n");
            sbEmptyBox.append(
                    verticalBorder
                            + emptySpace.repeat(padding)
                            + emptyBoxDisplay
                            + emptySpace.repeat(padding)
                            + verticalBorder
                            + "\n");

            sbEmptyBox.append(
                    leftBottomCornerBorder
                            + horizontalBorder.repeat(padding)
                            + horizontalBorder.repeat(emptyBoxDisplay.length())
                            + horizontalBorder.repeat(padding)
                            + rightBottomCornerBorder
                            + "\n");

            System.out.println(sbEmptyBox);
            return;
        }

        // Sort cards consistently by color and number
        List<Card> sortedBoard = new ArrayList<>(board);

        // Render top border of the box
        System.out.println(
                leftTopCornerBorder
                        + horizontalBorder.repeat(sortedBoard.size() * totalCardWidth + padding)
                        + rightTopCornerBorder);

        // Render index labels if requested
        if (showIndices) {
            sbIndices.append(verticalBorder);
            for (int i = 0; i < sortedBoard.size(); i++) {
                index = String.format("[%d]", i + 1);
                rightIndexPadding = totalCardWidth - index.length() - leftIndexPadding;

                sbIndices
                        .append(emptySpace.repeat(leftIndexPadding))
                        .append(
                                printConsoleColour(
                                        sortedBoard.get(i).getColour().toString().toLowerCase(),
                                        index))
                        .append(emptySpace.repeat(rightIndexPadding));
            }
            // Close the index row with a box edge
            sbIndices.append(emptySpace.repeat(padding) + verticalBorder);
            System.out.println(sbIndices);
        }

        // Build each card row line by line
        sbTop.append(verticalBorder);
        sbMiddle.append(verticalBorder);
        sbBottom.append(verticalBorder);

        for (Card card : sortedBoard) {
            sbTop.append(renderTopHalfCard(card, padding)).append(emptySpace);
            sbMiddle.append(renderMiddleCard(card, padding)).append(emptySpace);
            sbBottom.append(renderBottomHalfCard(card, padding)).append(emptySpace);
        }

        // Close the content rows
        sbTop.append(emptySpace.repeat(padding) + verticalBorder);
        sbMiddle.append(emptySpace.repeat(padding) + verticalBorder);
        sbBottom.append(emptySpace.repeat(padding) + verticalBorder);

        // Print all rows
        System.out.println(sbTop);
        System.out.println(sbMiddle);
        System.out.println(sbBottom);

        // Render bottom border of the box
        System.out.println(
                leftBottomCornerBorder
                        + horizontalBorder.repeat(sortedBoard.size() * totalCardWidth + padding)
                        + rightBottomCornerBorder);
    }

    /**
     * Renders a stack of cards in vertical columns by color, aligned with padding and boxed
     * borders. Used for parade and scoring zones (player boards).
     *
     * <p>Cards are stacked column-wise for each color, from top to bottom.
     *
     * @param board List of cards to render in columns.
     */
    public void printStackedCards(List<Card> board) {
        int padding = 3;
        int width = 5; // card width
        int totalCardWidth = padding + width + 2;

        String verticalBorder = "║";
        String rightTopCornerBorder = "╗";
        String leftTopCornerBorder = "╔";
        String rightBottomCornerBorder = "╝";
        String leftBottomCornerBorder = "╚";
        String horizontalBorder = "═";
        String emptySpace = " ";
        String emptyBoxDisplay = "No cards to display";

        // StringBuilders for different card parts
        StringBuilder sbEmptyBox = new StringBuilder();

        if (board == null || board.isEmpty()) {
            // Print an empty box layout if no cards are present

            sbEmptyBox.append(
                    leftTopCornerBorder
                            + horizontalBorder.repeat(padding)
                            + horizontalBorder.repeat(emptyBoxDisplay.length())
                            + horizontalBorder.repeat(padding)
                            + rightTopCornerBorder
                            + "\n");
            sbEmptyBox.append(
                    verticalBorder
                            + emptySpace.repeat(padding)
                            + emptyBoxDisplay
                            + emptySpace.repeat(padding)
                            + verticalBorder
                            + "\n");

            sbEmptyBox.append(
                    leftBottomCornerBorder
                            + horizontalBorder.repeat(padding)
                            + horizontalBorder.repeat(emptyBoxDisplay.length())
                            + horizontalBorder.repeat(padding)
                            + rightBottomCornerBorder
                            + "\n");

            System.out.println(sbEmptyBox);
            return;
        }

        // Sort cards for consistent stacking order
        board = new ArrayList<>(board);
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));

        // Group cards by colour
        Map<Colour, List<Card>> colourCardMap = new LinkedHashMap<>();
        for (Card card : board) {
            colourCardMap.computeIfAbsent(card.getColour(), k -> new ArrayList<>()).add(card);
        }

        // Get height of tallest stack
        int maxHeight = colourCardMap.values().stream().mapToInt(List::size).max().orElse(0);
        int boxWidth = colourCardMap.size() * totalCardWidth;

        // Print top border
        System.out.println(
                leftTopCornerBorder
                        + horizontalBorder.repeat(boxWidth + padding)
                        + rightTopCornerBorder);

        // Print each visual row of stacked cards
        for (int row = 0; row < maxHeight + 2; row++) {
            StringBuilder line = new StringBuilder(verticalBorder);

            for (List<Card> cards : colourCardMap.values()) {
                int count = cards.size();
                String part;

                if (row < count) {
                    part = renderTopHalfCard(cards.get(row), padding);
                } else if (row == count) {
                    part = renderMiddleCard(cards.get(count - 1), padding);
                } else if (row == count + 1) {
                    part = renderBottomHalfCard(cards.get(count - 1), padding);
                } else {
                    part = emptySpace.repeat(totalCardWidth - 1);
                }

                line.append(part);
            }

            // Close row with box edge
            line.append(emptySpace.repeat(padding + colourCardMap.size()));
            line.append(verticalBorder);
            System.out.println(line);
        }

        // Print bottom border
        System.out.println(
                leftBottomCornerBorder
                        + horizontalBorder.repeat(boxWidth + padding)
                        + rightBottomCornerBorder);
    }

    /**
     * Renders a full single card (3 rows: top, middle, bottom) as a string. This is mostly used
     * when displaying a standalone card (e.g., drawn card).
     *
     * @param card The card to render.
     * @param gaps The number of spaces to pad on the left and right of the card's content.
     * @return A full multiline string representing the card.
     */
    public String renderSingleCard(Card card, int gaps) {
        return System.lineSeparator()
                + renderTopHalfCard(card, gaps)
                + System.lineSeparator()
                + renderMiddleCard(card, gaps)
                + System.lineSeparator()
                + renderBottomHalfCard(card, gaps);
    }

    /**
     * Renders the top visual row of the card, showing the number and top border. The format looks
     * like: ╭3───╮
     *
     * @param card The card to render.
     * @param gaps Left and right spacing before the card begins (for alignment).
     * @return A colored string representing the top of the card.
     */
    public String renderTopHalfCard(Card card, int gaps) {
        String num = String.valueOf(card.getNumber());

        // "5" is the fixed internal width of the card. Subtract 1 for number, and rest are dashes.
        String top = " ".repeat(gaps) + "╭" + num + "─".repeat(5 - 1 - num.length()) + "╮";

        return printConsoleColour(card.getColour().toString().toLowerCase(), top);
    }

    /**
     * Renders the middle row of the card containing a themed emoji. The format looks like: | 🐇 |
     *
     * @param card The card to render.
     * @param gaps Left and right spacing before the card begins (for alignment).
     * @return A colored string representing the middle of the card with an emoji.
     */
    public String renderMiddleCard(Card card, int gaps) {
        int width = 5;
        String cardColour = "" + card.getColour();
        String lowerCardColour = cardColour.toLowerCase();
        String emoji =
                switch (card.getColour()) {
                    case Colour.BLACK -> "🐇"; // rabbit
                    case Colour.BLUE -> "👧"; // alice
                    case Colour.GREEN -> "🥚"; // egg
                    case Colour.RED -> "🎩"; // mad hatter
                    case Colour.YELLOW -> "🦆"; // dodo -> duck
                    case Colour.PURPLE -> "🐈"; // cat
                };

        String middle = " ".repeat(gaps) + "|" + " " + emoji + " " + "|";

        return printConsoleColour(card.getColour().toString().toLowerCase(), middle);
    }

    /**
     * Renders the bottom visual row of the card, forming the card’s lower border. The format looks
     * like: ╰────╯
     *
     * @param card The card to render.
     * @param gaps Left and right spacing before the card begins (for alignment).
     * @return A colored string representing the bottom of the card.
     */
    public String renderBottomHalfCard(Card card, int gaps) {
        String bottom = " ".repeat(gaps) + "╰" + "─".repeat(5 - 1) + "╯";
        return printConsoleColour(card.getColour().toString().toLowerCase(), bottom);
    }

    /**
     * Helper function to print a String into a certain colour
     *
     * @param colour Colour to turn the String into
     * @param colourisedString String to turn into a the colour set
     */
    public String printConsoleColour(String colour, String colourisedString) {
        return switch (colour) {
            case "red" -> Ansi.RED.apply(colourisedString);
            case "black" -> Ansi.BLACK.apply(colourisedString);
            case "green" -> Ansi.GREEN.apply(colourisedString);
            case "blue" -> Ansi.BLUE.apply(colourisedString);
            case "yellow" -> Ansi.YELLOW.apply(colourisedString);
            case "purple" -> Ansi.PURPLE.apply(colourisedString);
            default -> colourisedString; // no colour - white
        };
    }

    @Override
    public void renderRoll(int diceRoll1, int diceRoll2, List<Player> players) {
        String[] block = {
            "╔══════════╗",
            "║          ║",
            "║ ROLLING  ║",
            "║ DICE :)  ║",
            "║          ║",
            "╚══════════╝"
        };

        System.out.println("Shaking block...");
        System.out.println();

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
        printDicesHorizontally(returnDice(diceRoll1), returnDice(diceRoll2));
        super.renderRoll(diceRoll1, diceRoll2, players);
    }

    private void printBlockWithOffset(String[] block, int offset) {
        String space = " ".repeat(offset);
        for (String line : block) {
            System.out.println(space + line);
        }
    }

    // This just prints many new lines to "clear" the screen for shaking effect
    private void clearConsole() {
        System.out.print(Ansi.CLEAR);
        System.out.flush();
    }

    /**
     * Renders the dice with the given number.
     *
     * @param num The number on the dice (1-6).
     * @throws IllegalArgumentException if the number is not between 1 and 6.
     */
    private String[] returnDice(int num) {
        // Define each possible dice face
        String[] dice1 = {
            Ansi.apply("╔═════════╗", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║         ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║    o    ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║         ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("╚═════════╝", Ansi.BLACK, Ansi.WHITE_BACKGROUND)
        };

        String[] dice2 = {
            Ansi.apply("╔═════════╗", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o       ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║         ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║       o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("╚═════════╝", Ansi.BLACK, Ansi.WHITE_BACKGROUND)
        };

        String[] dice3 = {
            Ansi.apply("╔═════════╗", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o       ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║    o    ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║       o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("╚═════════╝", Ansi.BLACK, Ansi.WHITE_BACKGROUND)
        };

        String[] dice4 = {
            Ansi.apply("╔═════════╗", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o     o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║         ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o     o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("╚═════════╝", Ansi.BLACK, Ansi.WHITE_BACKGROUND)
        };

        String[] dice5 = {
            Ansi.apply("╔═════════╗", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o     o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║    o    ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o     o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("╚═════════╝", Ansi.BLACK, Ansi.WHITE_BACKGROUND)
        };

        String[] dice6 = {
            Ansi.apply("╔═════════╗", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o     o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o     o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("║ o     o ║", Ansi.BLACK, Ansi.WHITE_BACKGROUND),
            Ansi.apply("╚═════════╝", Ansi.BLACK, Ansi.WHITE_BACKGROUND)
        };

        return switch (num) {
            case 1 -> dice1;
            case 2 -> dice2;
            case 3 -> dice3;
            case 4 -> dice4;
            case 5 -> dice5;
            case 6 -> dice6;
            default -> throw new IllegalArgumentException("Dice number should be between 1-6");
        };
    }

    private void printDicesHorizontally(String[] dice1, String[] dice2) {
        for (int i = 0; i < dice1.length; i++) {
            System.out.println(dice1[i] + " ".repeat(5) + dice2[i]);
        }
    }

    /**
     * Renders the game ending screen with animation and final scores.
     *
     * @param playerScores final score map of all players
     */
    @Override
    public void renderEndGame(Map<AbstractPlayerController, Integer> playerScores) {
        try {
            for (int i = 0; i < 30; i++) {
                clearConsole();
                String[] asciiArt = {
                    "  ______ _____ _   _          _      ",
                    " |  ____|_   _| \\ | |   /\\   | |     ",
                    " | |__    | | |  \\| |  /  \\  | |     ",
                    " |  __|   | | | . ` | / /\\ \\ | |     ",
                    " | |     _| |_| |\\  |/ ____ \\| |____ ",
                    " |_|    |_____|_| \\_/_/    \\_\\______|",
                    "                                      ",
                    "                                      "
                };

                for (String line : asciiArt) {
                    System.out.println(Ansi.PURPLE.apply(line));
                }
                Thread.sleep(100);
            }

            for (int i = 0; i < 6; i++) {
                clearConsole();
                String[] asciiArt = {
                    "  ______ _____ _   _          _      ",
                    " |  ____|_   _| \\ | |   /\\   | |     ",
                    " | |__    | | |  \\| |  /  \\  | |     ",
                    " |  __|   | | | . ` | / /\\ \\ | |     ",
                    " | |     _| |_| |\\  |/ ____ \\| |____ ",
                    " |_|    |_____|_| \\_/_/    \\_\\______|",
                    "                                      ",
                    "                                      "
                };

                for (String line : asciiArt) {
                    System.out.println(Ansi.PURPLE.apply(line));
                }
            }

            int playerColWidth = 32;
            int scoreColWidth = 9;
            String header =
                    String.format(
                            "        ┌%s┐%n"
                                    + "        │ %-"
                                    + playerColWidth
                                    + "s │ %-"
                                    + scoreColWidth
                                    + "s │%n"
                                    + "        ├%s┤",
                            "─".repeat(playerColWidth + 2 + scoreColWidth + 3),
                            "Player",
                            "Score",
                            "─".repeat(playerColWidth + 2) + "┼" + "─".repeat(scoreColWidth + 2));
            System.out.println(header);

            for (Map.Entry<AbstractPlayerController, Integer> entry : playerScores.entrySet()) {
                System.out.printf(
                        "        │ %-" + playerColWidth + "s │ %" + scoreColWidth + "d │%n",
                        entry.getKey().getPlayer().getName(),
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
}
