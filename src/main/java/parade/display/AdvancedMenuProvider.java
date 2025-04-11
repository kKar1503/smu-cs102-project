package parade.display;

import parade.card.Card;
import parade.card.Colour;
import parade.display.option.LobbyMenuOption;
import parade.display.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

/**
 * AdvancedClientRenderer provides advanced rendering capabilities for the Parade game. It outputs
 * styled game content to the console.
 */
public class AdvancedMenuProvider implements MenuProvider {
    /**
     * Renders a stylized welcome message from an ASCII art file. Throws an exception if the file
     * cannot be found. Also displays a sample Parade card.
     */
    @Override
    public void renderWelcome() throws IllegalStateException {
        new AsciiWelcomeScreen().display();
    }

    @Override
    public MainMenuOption mainMenuPrompt() {
        MainMenu welcomeMenu = new MainMenu();
        return welcomeMenu.prompt();
    }

    /**
     * Displays the current players in the lobby and menu options. Indicates whether the lobby is
     * full or not ready.
     *
     * @param lobby List of players currently in the lobby.
     */
    @Override
    public LobbyMenuOption renderPlayersLobby(List<Player> lobby) {
        return new LobbyMenu(lobby).prompt();
    }

    /** Displays a prompt related to selecting computer difficulty. (Currently not implemented.) */
    @Override
    public void renderComputerDifficulty() {}

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
        // print player's name and drawn card
        System.out.println(System.lineSeparator() + player.getName() + "'s turn.");

        // Show the card that was drawn
        if (newlyDrawnCard != null) {
            System.out.println("You drew:" + renderSingleCard(newlyDrawnCard, 4));
        }

        // Display the parade line
        System.out.println(
                System.lineSeparator()
                        + "Parade"
                        + System.lineSeparator()
                        + "======================================================================");
        printCardsHorizontally(playCardData.getParade().getCards(), false);

        // Display the player's board, sorted by color and number
        List<Card> board = player.getBoard();
        board = new ArrayList<>(board);
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        System.out.println(
                System.lineSeparator()
                        + System.lineSeparator()
                        + "Your board"
                        + System.lineSeparator()
                        + "===========================================================================");
        printStackedCards(board);

        // Display the player's hand
        System.out.println(
                System.lineSeparator()
                        + System.lineSeparator()
                        + "Your hand"
                        + System.lineSeparator()
                        + "==========================================================================");
        printCardsHorizontally(playCardData.getParade().getCards(), true);

        // Prompt player for input
        System.out.printf("%n%nSelect a card to %s:", toDiscard ? "discard" : "play");
    }

    /** Displays a farewell message when the game ends. */
    @Override
    public void renderBye() {
        System.out.println(System.lineSeparator() + "THANK YOU FOR PLAYING! SEE YOU NEXT TIME!");
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

            if (options) {
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
        if (options) {
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
        board.sort(Comparator.comparing(Card::getColour));

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
                    if (colourCount + 1 == i) {
                        // This is the last card for this color, render bottom half
                        sb.append(renderMiddleCard(cardList.get(colourCount), padding));
                    } else if (colourCount + 2 == i) {
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
        return System.lineSeparator()
                + renderTopHalfCard(card, gaps)
                + System.lineSeparator()
                + renderMiddleCard(card, gaps)
                + System.lineSeparator()
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
        String emoji =
                switch (card.getColour()) {
                    case Colour.BLACK -> "🐇"; // rabbit
                    case Colour.BLUE -> "🧍‍♀️"; // alice
                    case Colour.GREEN -> "🥚"; // egg
                    case Colour.RED -> "🎩"; // mad hatter
                    case Colour.YELLOW -> "🦤"; // dodo
                    case Colour.PURPLE -> "🐈"; // cat
                };

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
