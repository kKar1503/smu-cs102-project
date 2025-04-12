package parade.menu.base;

import parade.exceptions.MenuCancelledException;
import parade.utils.Ansi;

import java.util.Scanner;

public abstract class AbstractPrompt<T> extends AbstractPrinter {
    protected static final Scanner sc = new Scanner(System.in);
    protected static final String PROMPT_MARKER =
            Ansi.RED.apply("(x to cancel) ") + Ansi.CYAN.apply("> ");

    public abstract T prompt() throws MenuCancelledException;

    protected String nextLine() throws MenuCancelledException {
        String input = sc.nextLine();
        if (input.equalsIgnoreCase("x")) {
            throw new MenuCancelledException();
        }
        return input;
    }
}
