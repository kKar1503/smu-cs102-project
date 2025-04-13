package parade.menu.menu;

import parade.card.Card;
import parade.exceptions.MenuCancelledException;
import parade.menu.base.AbstractMenu;
import parade.menu.prompt.NumericPrompt;
import parade.player.Player;
import parade.player.controller.PlayCardData;
import parade.utils.Ansi;

import java.util.*;

public class BasicPlayerTurnMenu extends AbstractMenu<Integer> {
    private final Player player;
    private final PlayCardData playCardData;
    private final boolean toDiscard;

    public BasicPlayerTurnMenu(Player player, PlayCardData playCardData, boolean toDiscard) {
        this.player = player;
        this.playCardData = playCardData;
        this.toDiscard = toDiscard;
    }

    @Override
    public Integer start() throws MenuCancelledException {
        // print player's name and drawn card
        println(NEW_LINE + player.getName() + "'s turn.");
        renderCardList(
                " Parade (read left to right, top to bottom!) ",
                playCardData.getParade().getCards());

        List<Card> board = new ArrayList<>(player.getBoard());
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        renderCardList(" Your scoring board ", board);
        renderCardList(" Cards in your hand ", player.getHand());

        printf("%n%nSelect a card to %s:", toDiscard ? "discard" : "play");
        flush();
        return new NumericPrompt(player.getHand().size()).prompt();
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
    void renderCardList(String label, List<Card> cards) {
        // Handle null or empty card list by rendering a placeholder box
        if (cards == null || cards.isEmpty()) {
            println();
            println("╔" + Ansi.PURPLE.apply(label) + "═".repeat(40) + "╗");
            println("║ No cards to display." + " ".repeat(39) + "║");
            println("╚" + "═".repeat(60) + "╝");
            flush();
            return;
        }

        // Layout constants
        final int cardWidth = 20; // Width of each individual card (in characters)
        final int spacing = 1; // Spacing between cards
        final int maxWidth = 100; // Maximum content width before wrapping

        // Determine how many cards can fit on a row given spacing constraints
        int cardsPerRow = Math.max(1, (maxWidth + spacing) / (cardWidth + spacing));
        int totalCards = cards.size();

        // Pre-render all card ASCII lines to optimize row-based printing
        List<String[]> renderedCards = new ArrayList<>();
        for (Card card : cards) {
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
        println(NEW_LINE + topBorder);

        // Render cards row by row
        for (int start = 0; start < totalCards; start += cardsPerRow) {
            int end = Math.min(start + cardsPerRow, totalCards);
            int actualCardsInRow = end - start;

            // Render index labels only for the player's hand to allow selection
            if (label.trim().equals("Cards in your hand")) {
                print("║ ");
                for (int j = start; j < end; j++) {
                    String index = "(" + (j + 1) + ")";
                    int padLeft = (cardWidth - index.length()) / 2;
                    int padRight = cardWidth - index.length() - padLeft;
                    print(" ".repeat(padLeft) + index + " ".repeat(padRight));
                    if (j < end - 1) print(" ");
                }

                // Pad the line if there are fewer than the expected number of cards
                int padCards = maxCardsInRow - actualCardsInRow;
                if (padCards > 0) {
                    int pad = padCards * (cardWidth + spacing);
                    print(" ".repeat(pad));
                }

                println(" ║");
            }

            // Render each line of the card's ASCII representation
            for (int line = 0; line < linesPerCard; line++) {
                print("║ ");
                for (int j = start; j < end; j++) {
                    print(renderedCards.get(j)[line]);
                    if (j < end - 1) print(" ");
                }

                // Pad any extra space if the current row has fewer cards than the max
                int padCards = maxCardsInRow - actualCardsInRow;
                if (padCards > 0) {
                    int pad = padCards * (cardWidth + spacing);
                    print(" ".repeat(pad));
                }

                println(" ║");
            }
        }

        // Print the bottom border to close the card box
        println(bottomBorder);
        flush();
    }

    /**
     * Colors a string with background based on the given color name.
     *
     * @param colour the name of the color
     * @param text the text to be colorized
     * @return colored string
     */
    String colorPrinter(String colour, String text) {
        return switch (colour) {
            case "RED" -> Ansi.apply(text, Ansi.RED_BACKGROUND, Ansi.BLACK);
            case "BLUE" -> Ansi.apply(text, Ansi.BLUE_BACKGROUND_BRIGHT, Ansi.BLACK);
            case "GREEN" -> Ansi.apply(text, Ansi.GREEN_BACKGROUND_BRIGHT, Ansi.BLACK);
            case "YELLOW" -> Ansi.apply(text, Ansi.YELLOW_BACKGROUND_BRIGHT, Ansi.BLACK);
            case "PURPLE" -> Ansi.apply(text, Ansi.PURPLE_BACKGROUND, Ansi.BLACK);
            default -> Ansi.BLACK_BACKGROUND.apply(text);
        };
    }

    /**
     * Returns the ASCII art representation of a single card.
     *
     * @param card the card to render
     * @return multi-line string representation
     */
    String[] rendersSingleCard(Card card) {
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
}
