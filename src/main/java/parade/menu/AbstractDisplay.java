package parade.menu;

import java.io.*;

abstract class AbstractDisplay {
    private static final PrintWriter out = new PrintWriter(System.out, false);

    static final String NEW_LINE = System.lineSeparator();

    void println(String s) {
        out.println(s);
    }

    void print(String s) {
        out.print(s);
    }

    void printf(String format, Object... args) {
        out.printf(format, args);
    }

    void flush() {
        out.flush();
    }

    void printlnFlush(String s) {
        out.println(s);
        out.flush();
    }

    void printFlush(String s) {
        out.print(s);
        out.flush();
    }

    void printfFlush(String format, Object... args) {
        out.printf(format, args);
        out.flush();
    }

    public abstract void display();
}
