package parade.textrenderer;

import com.google.gson.annotations.SerializedName;

public class LogInfo {
    private String message;
    private String caller;
    private String error;

    @SerializedName(value = "stack_trace")
    private String[] stackTrace;

    public LogInfo(String message) {
        this.message = message;
        this.caller = null;
        this.error = null;
        this.stackTrace = null;
    }

    public LogInfo(String message, Exception error) {
        this.message = message;
        this.error = error.toString();
        this.stackTrace = formatStackTrace(error.getStackTrace());
        this.caller = null;
    }

    public LogInfo(String message, StackTraceElement caller) {
        this(message);
        this.caller = caller.toString();
    }

    public LogInfo(String message, Exception error, StackTraceElement caller) {
        this(message, error);
        this.caller = caller.toString();
    }

    private String[] formatStackTrace(StackTraceElement[] stackTrace) {
        String[] formattedStackTrace = new String[stackTrace.length];
        for (int i = 0; i < stackTrace.length; i++) {
            formattedStackTrace[i] = stackTrace[i].toString();
        }
        return formattedStackTrace;
    }
}
