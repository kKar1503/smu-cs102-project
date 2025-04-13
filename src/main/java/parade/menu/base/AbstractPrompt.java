package parade.menu.base;

import parade.exception.MenuCancelledException;
import parade.utils.Ansi;

public abstract class AbstractPrompt<T> extends AbstractPrinter {
    protected static final String PROMPT_MARKER = Ansi.CYAN.apply("> ");
    protected static final String CANCELLABLE_PROMPT_MARKER =
            Ansi.RED.apply("(x to cancel) ") + PROMPT_MARKER;

    protected final boolean cancellable;

    public AbstractPrompt() {
        this(false);
    }

    public AbstractPrompt(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public abstract T prompt() throws MenuCancelledException;

    protected String nextLine() throws MenuCancelledException {
        String input = reader.readLine(cancellable ? CANCELLABLE_PROMPT_MARKER : PROMPT_MARKER);
        if (cancellable && input.equalsIgnoreCase("x")) {
            throw new MenuCancelledException();
        }
        return input;
    }
}
