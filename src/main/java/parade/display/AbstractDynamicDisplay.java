package parade.display;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.io.IOException;

abstract class AbstractDynamicDisplay extends AbstractDisplay {
    private static final AbstractLogger LOGGER = LoggerProvider.getInstance();

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 24;

    final int terminalWidth;
    final int terminalHeight;

    AbstractDynamicDisplay() {
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
}
