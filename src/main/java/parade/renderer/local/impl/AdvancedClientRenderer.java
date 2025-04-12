package parade.renderer.local.impl;

import parade.card.Card;
import parade.card.Colour;
import parade.player.Player;
import parade.player.controller.PlayCardData;
import parade.renderer.local.ClientRenderer;
import parade.utils.ConsoleColors;

import java.io.InputStream;
import java.util.*;

/**
 * AdvancedClientRenderer provides advanced rendering capabilities for the Parade game. It outputs
 * styled game content to the console.
 */
public class AdvancedClientRenderer implements ClientRenderer {
    // Constructor: Initializes the renderer
    public AdvancedClientRenderer() {}

    /**
     * Renders a plain message without a line break.
     *
     * @param message The message to print.
     */
    @Override
    public void render(String message) {
        System.out.print(message);
    }

    /**
     * Renders a message followed by a line break.
     *
     * @param message The message to print.
     */
    @Override
    public void renderln(String message) {
        System.out.println(message);
    }

    /**
     * Renders a formatted message using the specified format and arguments.
     *
     * @param format The format string.
     * @param args Arguments referenced by the format specifiers in the format string.
     */
    @Override
    public void renderf(String format, Object... args) {
        System.out.printf(format, args);
    }

    /**
     * Renders a stylized welcome message from an ASCII art file. Throws an exception if the file
     * cannot be found. Also displays a sample Parade card.
     */
    @Override
    public void renderWelcome() throws IllegalStateException {
        // Load the ASCII art welcome banner from the resource file
        InputStream inFromFile =
                getClass().getClassLoader().getResourceAsStream("parade_ascii_art.txt");
        if (inFromFile == null) {
            throw new IllegalStateException("parade_ascii_art.txt not found");
        }
        Scanner s = new Scanner(inFromFile).useDelimiter("\\Z");
        String paradeWelcome = s.hasNext() ? s.next() : "";

        // Render the welcome banner with styling
        if (paradeWelcome != null) {
            System.out.println(
                    ConsoleColors.PURPLE_BOLD
                            + "============================= Welcome to Parade!"
                            + " =============================="
                            + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + paradeWelcome + ConsoleColors.RESET);
            System.out.println(
                    "===================================================================================");

            // Print a sample card
            System.out.println(renderSingleCard(new Card(1, Colour.BLACK), 4));
        }
    }

    /** Displays the main menu options to the user. */
    @Override
    public void renderMenu() {
        System.out.println("1. Start Game");
        System.out.println("2. Exit");
        System.out.print("Please select an option: ");
    }

    /**
     * Displays the list of current players in the lobby and presents menu options for lobby
     * management. This includes the ability to add human or computer players, remove existing
     * players, and start the game if conditions are met.
     *
     * <p>Menu options are context-sensitive: - If the lobby is full (6 players), add options are
     * visually disabled. - If the lobby is empty, remove option is disabled. - If fewer than 2
     * players are present, the start option is disabled.
     *
     * @param lobby List of players currently in the lobby.
     */
    @Override
    public void renderPlayersLobby(List<Player> lobby) {
        System.out.println("Players in lobby: ");
        for (int i = 1; i <= lobby.size(); i++) {
            System.out.printf("%d. %s%n", i, lobby.get(i - 1).getName());
        }
        System.out.println();

        // Render context-aware menu options based on lobby state
        System.out.println("1. Add Player" + (lobby.size() == 6 ? " (Lobby is full)" : ""));
        System.out.println("2. Add Computer" + (lobby.size() == 6 ? " (Lobby is full)" : ""));
        System.out.println(
                "3. Remove player/computer" + (lobby.isEmpty() ? " (Lobby is empty)" : ""));
        System.out.println("4. Start Game" + (lobby.size() < 2 ? " (Not enough players)" : ""));
        System.out.print("Please select an option: ");
    }

    /**
     * Prompts the user to choose a difficulty level for a newly added computer player. This method
     * ensures consistency in user experience between renderers and is critical to correctly
     * initializing AI behavior.
     */
    @Override
    public void renderComputerDifficulty() {
        System.out.println("Choose computer player's difficulty");
        System.out.println("1. Easy");
        System.out.println("2. Normal");
        System.out.println("3. Hard");
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
            case "red" -> ConsoleColors.red(colourisedString);
            case "black" -> ConsoleColors.black(colourisedString);
            case "green" -> ConsoleColors.green(colourisedString);
            case "blue" -> ConsoleColors.blue(colourisedString);
            case "yellow" -> ConsoleColors.yellow(colourisedString);
            case "purple" -> ConsoleColors.purple(colourisedString);
            default -> colourisedString; // no colour - white
        };
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
    }

    private void printBlockWithOffset(String[] block, int offset) {
        String space = " ".repeat(offset);
        for (String line : block) {
            System.out.println(space + line);
        }
    }

    // This just prints many new lines to "clear" the screen for shaking effect
    private void clearConsole() {
        System.out.print(ConsoleColors.CLEAR);
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

    /**
     * Renders the game ending screen with animation and final scores.
     *
     * @param playerScores final score map of all players
     */
    @Override
    public void renderEndGame(Map<Player, Integer> playerScores) {
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
                    System.out.println(ConsoleColors.purple(line));
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
                    System.out.println(ConsoleColors.purple(line));
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

            for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
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
}
