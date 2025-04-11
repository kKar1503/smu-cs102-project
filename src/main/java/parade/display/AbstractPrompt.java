package parade.display;

import java.util.Scanner;

abstract class AbstractPrompt<T> extends AbstractDisplay {
    static final Scanner sc = new Scanner(System.in);

    public abstract T prompt();
}
