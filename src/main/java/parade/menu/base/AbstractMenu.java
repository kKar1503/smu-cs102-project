package parade.menu.base;

import parade.exceptions.MenuCancelledException;

public abstract class AbstractMenu<T> extends AbstractPrinter {
    public abstract T start() throws MenuCancelledException;
}
