package parade.menu.base;

import parade.exception.MenuCancelledException;

public abstract class AbstractMenu<T> extends AbstractPrinter {
    public abstract T start() throws MenuCancelledException;
}
