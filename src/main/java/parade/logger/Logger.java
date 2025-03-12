package parade.logger;

import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Logger {
    private static final String LOGGER_PACKAGE = Logger.class.getPackage().getName();

    private final PrintWriter writer;

    public Logger() {
        // Defaults to no-op logger
        this(
                new Writer() {
                    @Override
                    public void write(char[] cbuf, int off, int len) {}

                    @Override
                    public void flush() {}

                    @Override
                    public void close() {}
                });
    }

    public Logger(String fileName) throws FileNotFoundException {
        this.writer = new PrintWriter(fileName);
    }

    public Logger(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    public Logger(OutputStream out) {
        this.writer = new PrintWriter(out);
    }

    public void log(String message) {
        writelnFlush(new LogInfo(message));
    }

    public void log(String message, Exception e) {
        writelnFlush(new LogInfo(message, e));
    }

    public void logf(String format, Object... args) {
        writelnFlush(new LogInfo(String.format(format, args)));
    }

    protected void writelnFlush(Object x) {
        writelnFlush(String.valueOf(x));
    }

    protected void writelnFlush(String message) {
        writer.println(message);
        writer.flush();
    }

    protected void write(String message) {
        writer.print(message);
    }

    protected void writeln(String message) {
        writer.println(message);
    }

    protected void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

    protected class LogInfo {
        private String message;
        private String caller;
        private String error;
        private Date timestamp;

        @SerializedName(value = "stack_trace")
        private String[] stackTrace;

        public LogInfo(String message) {
            this.message = message;
            this.caller = deriveCaller();
            this.timestamp = new Date();
        }

        public LogInfo(String message, Exception error) {
            this(message);
            this.error = error.toString();
            this.stackTrace = formatStackTrace(error.getStackTrace());
        }

        public String getMessage() {
            return message;
        }

        public String getCaller() {
            return caller;
        }

        public String getError() {
            return error;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String[] getStackTrace() {
            return stackTrace;
        }

        private String deriveCaller() {
            Predicate<StackWalker.StackFrame> logFrameFilter =
                    frame -> !frame.getClassName().startsWith(LOGGER_PACKAGE);
            Function<StackWalker.StackFrame, String> stackFrameMapper =
                    frame -> frame.toStackTraceElement().toString();
            return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                    .walk(
                            frames ->
                                    frames.filter(logFrameFilter)
                                            .findFirst()
                                            .map(stackFrameMapper)
                                            .orElse("Unknown"));
        }

        private String[] formatStackTrace(StackTraceElement[] stackTrace) {
            String[] formattedStackTrace = new String[stackTrace.length];
            for (int i = 0; i < stackTrace.length; i++) {
                formattedStackTrace[i] = stackTrace[i].toString();
            }
            return formattedStackTrace;
        }

        @Override
        public String toString() {
            return "LogInfo{"
                    + "message='"
                    + message
                    + "', caller='"
                    + caller
                    + "', error='"
                    + error
                    + "', stackTrace="
                    + Arrays.toString(stackTrace)
                    + '}';
        }
    }
}
