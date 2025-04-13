package parade.menu.base;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.*;

import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.utils.Ansi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractPrinter {
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();
    protected static final PrintWriter out;
    protected static final LineReader reader;

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 24;

    protected static final String NEW_LINE = System.lineSeparator();
    protected static final int DEFAULT_FRAME_DELAY_MS = 5000;
    private static final String SKIP_SLEEP_TEXT = "Press Enter to continue...";
    protected static final int terminalWidth;
    protected static final int terminalHeight;
    protected static final Terminal terminal;

    static { // Static block, think of this like a constructor but static, run once EVER.
        int width;
        int height;
        Terminal term = null;
        LineReader lr = null;
        PrintWriter pw = new PrintWriter(System.out, false);
        try {
            term = TerminalBuilder.builder().system(true).jansi(true).build();
            width = term.getWidth();
            height = term.getHeight();
            pw = term.writer();
            Logger.getLogger("org.jline").setLevel(Level.OFF);
            lr = LineReaderBuilder.builder().terminal(term).build();
            LOGGER.log("Initialised terminal instance with JLine");
        } catch (IOException e) {
            LOGGER.log("Failed to fetch terminal dimensions, defaulting to 80x24", e);
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
        }
        terminal = term;
        out = pw;
        reader = lr;
        terminalWidth = width;
        terminalHeight = height;
    }

    public AbstractPrinter() {}

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
        terminal.flush();
    }

    protected void printlnFlush(String s) {
        println(s);
        flush();
    }

    protected void printlnFlush() {
        println();
        flush();
    }

    protected void printFlush(String s) {
        print(s);
        flush();
    }

    protected void printfFlush(String format, Object... args) {
        printf(format, args);
        flush();
    }

    protected void clear() {
        printFlush(Ansi.CLEAR.toString());
    }

    protected void moveCursor() {
        printFlush(Ansi.MOVE_CURSOR_TO_TOP_LEFT.toString());
    }

    protected void moveCursor(int row, int col) {
        printfFlush(Ansi.MOVE_CURSOR_TO_FORMAT.toString(), row, col);
    }

    protected void sleep() {
        sleep(DEFAULT_FRAME_DELAY_MS, true);
    }

    protected void sleep(int millis) {
        sleep(millis, false);
    }

    protected void sleep(int millis, boolean cancellable) {
        if (cancellable && terminal != null) {
            cancellableSleep(millis);
        } else {
            nonCancellableSleep(millis);
        }
    }

    private void nonCancellableSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.log("Sleep interrupted", e);
        }
    }

    private void cancellableSleep(int millis) {
        moveCursor(terminalHeight, terminalWidth - SKIP_SLEEP_TEXT.length());
        out.print(SKIP_SLEEP_TEXT);
        moveCursor();
        terminal.flush();

        try (ExecutorService executor = Executors.newFixedThreadPool(2); ) {
            boolean timedOut =
                    executor.invokeAny(Arrays.asList(this::keyPressed, () -> timedOut(millis)));
            if (timedOut) {
                LOGGER.log("Sleep completed successfully at full duration");
            } else {
                LOGGER.log("Sleep interrupted by key press");
            }
            executor.shutdownNow();
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.log("Sleep interrupted", e);
        }
    }

    private boolean keyPressed() {
        try {
            reader.readLine();
            return false; // Indicates sleep was interrupted
        } catch (Exception e) {
            return true; // Treat exceptions as timeout
        }
    }

    private boolean timedOut(int millis) {
        try {
            Thread.sleep(millis);
            return true; // Indicates timeout occurred
        } catch (InterruptedException e) {
            return false; // Indicates interrupted
        }
    }
}
