package parade.renderer.log;

import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LogRenderer {
    private static final String DEBUG_PACKAGE = LogRenderer.class.getPackage().getName();

    private final PrintWriter writer;

    public LogRenderer() {
        // Defaults to no-op debug renderer
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

    public LogRenderer(String fileName) throws FileNotFoundException {
        this.writer = new PrintWriter(fileName);
    }

    public LogRenderer(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    public LogRenderer(OutputStream out) {
        this.writer = new PrintWriter(out);
    }

    public void debug(String message) {
        writelnFlush(new LogInfo(message));
    }

    public void debug(String message, Exception e) {
        writelnFlush(new LogInfo(message, e));
    }

    public void debugf(String format, Object... args) {
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
            Predicate<StackWalker.StackFrame> debugFrameFilter =
                    frame -> !frame.getClassName().startsWith(DEBUG_PACKAGE);
            Function<StackWalker.StackFrame, String> stackFrameMapper =
                    frame -> frame.toStackTraceElement().toString();
            return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                    .walk(
                            frames ->
                                    frames.filter(debugFrameFilter)
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
