package parade.logger.impl;

import parade.logger.AbstractLogger;
import parade.utils.ConsoleColors;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PrettyLogger extends AbstractLogger {
    public PrettyLogger() {
        super(new PrintWriter(System.out));
    }

    @Override
    public void log(String message) {
        writelnFlush(format(message));
    }

    @Override
    public void log(String message, Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(format(message)).append(System.lineSeparator());
        Predicate<StackTraceElement> loggerFrameFilter =
                elem -> !elem.getClassName().startsWith(this.getClass().getPackageName());
        List<String> stackTrace =
                Stream.of(e.getStackTrace())
                        .filter(loggerFrameFilter)
                        .map(StackTraceElement::toString)
                        .toList();
        sb.append(ConsoleColors.RED).append(e).append(System.lineSeparator());
        stackTrace.forEach(x -> sb.append("    ").append(x).append(System.lineSeparator()));
        sb.append(ConsoleColors.RESET);
        writelnFlush(sb.toString());
    }

    @Override
    public void logf(String format, Object... args) {
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
