package parade.menu.prompt;

import parade.menu.base.AbstractPrompt;

import java.util.*;

public class StringPrompt extends AbstractPrompt<String> {
    private final boolean rejectEmptyInput;

    public StringPrompt() {
        this(true);
    }

    public StringPrompt(boolean acceptEmptyInput) {
        this.rejectEmptyInput = acceptEmptyInput;
    }

    public String prompt() {
        while (true) {
            try {
                printFlush(PROMPT_MARKER);
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
