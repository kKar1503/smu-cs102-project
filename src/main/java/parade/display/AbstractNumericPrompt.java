package parade.display;

import java.util.*;

abstract class AbstractNumericPrompt<T> extends AbstractPrompt<T> {
    private final String displayText;
    private final int optionCount;

    AbstractNumericPrompt(String[] options) {
        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("Options cannot be null or empty");
        }
        for (int i = 0; i < options.length; i++) {
            options[i] = String.format("%d. %s", i + 1, options[i]);
        }
        this.displayText = String.join(NEW_LINE, options);
        this.optionCount = options.length;
    }

    AbstractNumericPrompt(String displayText, int optionCount) {
        this.displayText = displayText;
        this.optionCount = optionCount;
    }

    @Override
    public void display() {
        println(displayText);
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
    int promptForInput() {
        int choice = -1;
        while (choice < 1 || choice > optionCount) {
            try {
                display();
                printFlush(NEW_LINE + "> ");
                String line = sc.nextLine();
                choice = Integer.parseInt(line);
                if (choice < 0 || choice > optionCount) {
                    throw new NoSuchElementException();
                }
            } catch (NoSuchElementException | NumberFormatException e) {
                println("Invalid input. Please enter a number between 1 and " + optionCount);
                printlnFlush();
            }
        }
        return choice - 1;
    }
}
