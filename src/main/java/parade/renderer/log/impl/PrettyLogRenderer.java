package parade.renderer.log.impl;

import parade.renderer.ConsoleColors;
import parade.renderer.log.LogRenderer;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PrettyLogRenderer extends LogRenderer {
    public PrettyLogRenderer() {
        super(new PrintWriter(System.out));
    }

    @Override
    public void debug(String message) {
        writelnFlush(format(message));
    }

    @Override
    public void debug(String message, Exception e) {
        writeln(format(message));
        Predicate<StackTraceElement> debugFrameFilter =
                elem -> !elem.getClassName().startsWith(this.getClass().getPackageName());
        List<String> stackTrace =
                Stream.of(e.getStackTrace())
                        .filter(debugFrameFilter)
                        .map(StackTraceElement::toString)
                        .toList();
        writeln(ConsoleColors.RED + stackTrace.getFirst());
        stackTrace.stream().skip(1).forEach(x -> writeln("    " + x));
        write(ConsoleColors.RESET);
        flush();
    }

    @Override
    public void debugf(String format, Object... args) {
        writelnFlush(format(String.format(format, args)));
    }

    private String format(String message) {
        LogInfo logInfo = new LogInfo(message); // use LogInfo to retrieve the caller and timestamp
        return ConsoleColors.GREY
                + new SimpleDateFormat("HH:mma").format(logInfo.getTimestamp()).toUpperCase()
                + ConsoleColors.RESET
                + " "
                + logInfo.getCaller()
                + " "
                + ConsoleColors.CYAN
                + " > "
                + ConsoleColors.RESET
                + message;
    }
}
