package parade.menu.base;

import parade.exceptions.MenuCancelledException;
import parade.utils.Ansi;

public abstract class AbstractPrompt<T> extends AbstractPrinter {
    protected static final String PROMPT_MARKER =
            Ansi.RED.apply("(x to cancel) ") + Ansi.CYAN.apply("> ");

    public abstract T prompt() throws MenuCancelledException;

    protected String nextLine() throws MenuCancelledException {
        String input = reader.readLine();
        if (input.equalsIgnoreCase("x")) {
            throw new MenuCancelledException();
        }
        return input;
    }
}
