package parade.menu;

import java.util.*;

abstract class AbstractNumericPrompt extends AbstractDisplay {
    private static final Scanner sc = new Scanner(System.in);

    private final String[] options;

    /**
     * Constructor for AbstractNumericInteractiveMenu.
     *
     * @param options the options to display in the menu
     */
    AbstractNumericPrompt(String[] options) {
        this.options = options;
    }

    @Override
    public void display() {
        for (int i = 1; i <= options.length; i++) {
            printf("%d. %s%n", i, options[i]);
        }
        flush();
    }

    /**
     * Prompt the user for a number between 1 to n (inclusive), where 1 is the first option and n is
     * the length of options.
     *
     * <p>This method calls the display() method to show the options to the user, and then waits for
     * the user to input a number.
     *
     * @return the index of the chosen option (0-based)
     */
    int prompt() {
        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("Options cannot be null or empty");
        }

        int choice = -1;
        while (choice < 1 || choice > options.length) {
            try {
                printFlush(NEW_LINE + "> ");
                String line = sc.nextLine();
                try {
                    choice = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    printlnFlush(
                            "Invalid input. Please enter a number between 1 and " + options.length);
                }
            } catch (NoSuchElementException e) {
                printlnFlush("Input error. Please try again.");
            }
        }
        return choice - 1;
    }
}
