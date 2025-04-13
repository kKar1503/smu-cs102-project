package parade.menu.prompt;

import parade.menu.base.AbstractPrompt;

import java.util.*;

public class StringPrompt extends AbstractPrompt<String> {
    private final boolean rejectEmptyInput;

    public StringPrompt() {
        this(true, false);
    }

    public StringPrompt(boolean rejectEmptyInput, boolean cancellable) {
        super(cancellable);
        this.rejectEmptyInput = rejectEmptyInput;
    }

    @Override
    public String prompt() {
        while (true) {
            try {
                String input = nextLine();

                if (rejectEmptyInput && input.isEmpty()) {
                    printlnFlush("Input cannot be empty. Please try again.");
                    continue;
                }

                return input;
            } catch (NoSuchElementException e) {
                println("Unable to read input.");
                printlnFlush();
            }
        }
    }
}
