package parade.menu.prompt;

public class OptionsPrompt extends NumericPrompt {
    final String displayText;

    public OptionsPrompt(String... options) {
        super(options.length);
        if (options.length == 0) {
            throw new IllegalArgumentException("Options cannot be empty");
        }
        for (int i = 0; i < options.length; i++) {
            options[i] = String.format("%d. %s", i + 1, options[i]);
        }
        this.displayText = String.join(NEW_LINE, options);
    }

    public OptionsPrompt(String displayText, int optionCount) {
        super(optionCount);
        this.displayText = displayText;
    }

    /**
     * Prompt the user for a number between 1 to n+1 (inclusive), where 1 is the first option and n
     * is the length of options. The last option is always "Go back".
     *
     * @return the index of the chosen option (0-based)
     */
    @Override
    public Integer prompt() {
        printlnFlush(displayText);
        return super.prompt();
    }
}
