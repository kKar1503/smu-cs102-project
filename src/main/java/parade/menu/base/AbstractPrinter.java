package parade.menu.base;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.utils.Ansi;

import java.io.IOException;
import java.io.PrintWriter;

abstract class AbstractPrinter {
    private static final PrintWriter out = new PrintWriter(System.out, false);
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 24;

    protected final int terminalWidth;
    protected final int terminalHeight;

    protected static final String NEW_LINE = System.lineSeparator();

    AbstractPrinter() {
        int terminalWidth;
        int terminalHeight;
        try (Terminal terminal = TerminalBuilder.builder().build()) {
            terminalWidth = terminal.getWidth();
            terminalHeight = terminal.getHeight();
        } catch (IOException e) {
            LOGGER.log("Failed to fetch terminal dimensions, defaulting to 80x24", e);
            terminalWidth = DEFAULT_WIDTH; // Default width
            terminalHeight = DEFAULT_HEIGHT; // Default height
        }
        this.terminalWidth = terminalWidth;
        this.terminalHeight = terminalHeight;
    }

    protected void println(String s) {
        out.println(s);
    }

    protected void println() {
        out.println();
    }

    protected void print(String s) {
        out.print(s);
    }

    protected void printf(String format, Object... args) {
        out.printf(format, args);
    }

    protected void flush() {
        out.flush();
    }

    protected void printlnFlush(String s) {
        out.println(s);
        out.flush();
    }

    protected void printlnFlush() {
        out.println();
        out.flush();
    }

    protected void printFlush(String s) {
        out.print(s);
        out.flush();
    }

    protected void printfFlush(String format, Object... args) {
        out.printf(format, args);
        out.flush();
    }

    protected void clear() {
        out.print(Ansi.CLEAR);
        out.flush();
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.log("Error while sleeping", e);
        }
    }
}
