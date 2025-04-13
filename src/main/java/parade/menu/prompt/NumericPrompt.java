package parade.menu.prompt;

import parade.menu.base.AbstractPrompt;

import java.util.*;

public class NumericPrompt extends AbstractPrompt<Integer> {
    private final int promptRange;

    public NumericPrompt(int promptRange) {
        this(promptRange, false);
    }

    public NumericPrompt(int promptRange, boolean cancellable) {
        super(cancellable);
        if (promptRange <= 0) {
            throw new IllegalArgumentException("Prompt range must be greater than 0");
        }
        this.promptRange = promptRange;
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
    public Integer prompt() {
        while (true) {
            try {
                String line = nextLine();
                int choice = Integer.parseInt(line);
                if (choice < 1 || choice > promptRange) {
                    throw new NoSuchElementException();
                }
                return choice - 1;
            } catch (NoSuchElementException | NumberFormatException e) {
                println("Invalid input. Please enter a number between 1 and " + promptRange);
                printlnFlush();
            }
        }
    }
}
