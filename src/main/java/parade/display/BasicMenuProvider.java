package parade.display;

import parade.card.Card;
import parade.display.option.LobbyMenuOption;
import parade.display.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

/**
 * A basic text-based implementation of the client renderer for local gameplay. Responsible for
 * displaying game state and prompting the user via console.
 */
public class BasicMenuProvider implements MenuProvider {
    /**
     * Renders the welcome banner for the game. Throws an exception if file is missing.
     *
     * @throws IllegalStateException if the resource file cannot be found
     */
    @Override
    public void renderWelcome() throws IllegalStateException {
        new DynamicSeparator("✨ Welcome to Parade! ✨", Ansi.PURPLE::apply);
    }

    @Override
    public MainMenuOption mainMenuPrompt() {
        return new AsciiMainMenu().prompt();
    }

    /**
     * Displays the list of players in the lobby and available actions.
     *
     * @param lobby list of players currently in the lobby
     */
    @Override
    public LobbyMenuOption renderPlayersLobby(List<Player> lobby) {
        return new LobbyMenu(lobby).prompt();
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
     * @param playCardData the data for the current turn
     * @param toDiscard indicates if the player should discard a card
     */
    @Override
    public void renderPlayerTurn(
            Player player, Card newlyDrawnCard, PlayCardData playCardData, boolean toDiscard) {
        // print player's name and drawn card
        System.out.println(System.lineSeparator() + player.getName() + "'s turn.");
        if (newlyDrawnCard != null) {
            System.out.println("You drew:" + System.lineSeparator());
            String[] formattedCard = rendersSingleCard(newlyDrawnCard);
            for (String line : formattedCard) {
                System.out.println(line);
            }
        }

        renderCardList(
                " Parade (read left to right, top to bottom!) ",
                playCardData.getParade().getCards());

        List<Card> board = new ArrayList<>(player.getBoard());
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        renderCardList(" Your scoring board ", board);
        renderCardList(" Cards in your hand ", player.getHand());

        System.out.printf("%n%nSelect a card to %s:", toDiscard ? "discard" : "play");
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
     * Renders a list of cards horizontally within a styled bordered box. Cards are rendered using
     * their ASCII representation, with automatic line wrapping and optional index labeling for card
     * selection. The cards are sorted using their natural order as defined by the Comparable
     * implementation.
     *
     * @param label the section label to be displayed as the header of the box
     * @param cards the list of cards to render
     */
    public void renderCardList(String label, List<Card> cards) {
        // Handle null or empty card list by rendering a placeholder box
        if (cards == null || cards.isEmpty()) {
            System.out.println();
            System.out.println("╔" + Ansi.PURPLE.apply(label) + "═".repeat(40) + "╗");
            System.out.println("║ No cards to display." + " ".repeat(39) + "║");
            System.out.println("╚" + "═".repeat(60) + "╝");
            return;
        }

        // Defensive copy to avoid modifying unmodifiable lists
        List<Card> sortedCards = new ArrayList<>(cards);

        // Sort cards using their natural order (by color then number)
        Collections.sort(sortedCards);

        // Layout constants
        final int cardWidth = 20; // Width of each individual card (in characters)
        final int spacing = 1; // Spacing between cards
        final int maxWidth = 100; // Maximum content width before wrapping

        // Determine how many cards can fit on a row given spacing constraints
        int cardsPerRow = Math.max(1, (maxWidth + spacing) / (cardWidth + spacing));
        int totalCards = sortedCards.size();

        // Pre-render all card ASCII lines to optimize row-based printing
        List<String[]> renderedCards = new ArrayList<>();
        for (Card card : sortedCards) {
            renderedCards.add(rendersSingleCard(card));
        }

        // Each card is rendered over a fixed number of lines
        int linesPerCard = renderedCards.get(0).length;

        // Force at least 4 cards per row to maintain layout consistency for small hands
        int maxCardsInRow = Math.max(4, Math.min(cardsPerRow, totalCards));
        int contentWidth = maxCardsInRow * cardWidth + (maxCardsInRow - 1) * spacing;

        // Construct top and bottom borders dynamically based on label and width
        String topBorder =
                "╔"
                        + Ansi.PURPLE.apply(label)
                        + "═".repeat(Math.max(0, contentWidth - label.trim().length()))
                        + "╗";
        String bottomBorder = "╚" + "═".repeat(contentWidth + 2) + "╝";

        // Print the top border with the section label
        System.out.println(System.lineSeparator() + topBorder);

        // Render cards row by row
        for (int start = 0; start < totalCards; start += cardsPerRow) {
            int end = Math.min(start + cardsPerRow, totalCards);
            int actualCardsInRow = end - start;

            // Render index labels only for the player's hand to allow selection
            if (label.trim().equals("Cards in your hand")) {
                System.out.print("║ ");
                for (int j = start; j < end; j++) {
                    String index = "(" + (j + 1) + ")";
                    int padLeft = (cardWidth - index.length()) / 2;
                    int padRight = cardWidth - index.length() - padLeft;
                    System.out.print(" ".repeat(padLeft) + index + " ".repeat(padRight));
                    if (j < end - 1) System.out.print(" ");
                }

                // Pad the line if there are fewer than the expected number of cards
                int padCards = maxCardsInRow - actualCardsInRow;
                if (padCards > 0) {
                    int pad = padCards * (cardWidth + spacing);
                    System.out.print(" ".repeat(pad));
                }

                System.out.println(" ║");
            }

            // Render each line of the card's ASCII representation
            for (int line = 0; line < linesPerCard; line++) {
                System.out.print("║ ");
                for (int j = start; j < end; j++) {
                    System.out.print(renderedCards.get(j)[line]);
                    if (j < end - 1) System.out.print(" ");
                }

                // Pad any extra space if the current row has fewer cards than the max
                int padCards = maxCardsInRow - actualCardsInRow;
                if (padCards > 0) {
                    int pad = padCards * (cardWidth + spacing);
                    System.out.print(" ".repeat(pad));
                }

                System.out.println(" ║");
            }
        }

        // Print the bottom border to close the card box
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
            case "RED" -> Ansi.RED_BACKGROUND.apply(text);
            case "BLUE" -> Ansi.BLUE_BACKGROUND_BRIGHT.apply(text);
            case "GREEN" -> Ansi.GREEN_BACKGROUND_BRIGHT.apply(text);
            case "YELLOW" -> Ansi.YELLOW_BACKGROUND_BRIGHT.apply(text);
            case "PURPLE" -> Ansi.PURPLE_BACKGROUND.apply(text);
            default -> Ansi.BLACK_BACKGROUND.apply(text);
        };
    }

    /**
     * Returns the ASCII art representation of a single card.
     *
     * @param card the card to render
     * @return multi-line string representation
     */
    public String[] rendersSingleCard(Card card) {
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

        return Arrays.stream(lines)
                .map(line -> colorPrinter(colorCode, line))
                .toArray(String[]::new);
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

        System.out.println("Shaking block...");

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
        System.out.print(Ansi.CLEAR);
        System.out.flush();
    }

    /** Renders a simple farewell message at the end of the game session. */
    @Override
    public void renderBye() {
        System.out.println(System.lineSeparator() + "THANK YOU FOR PLAYING! SEE YOU NEXT TIME!");
    }
}
