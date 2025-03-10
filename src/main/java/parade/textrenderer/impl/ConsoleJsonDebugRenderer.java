package parade.textrenderer.impl;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import parade.settings.SettingKey;
import parade.settings.Settings;
import parade.textrenderer.DebugRenderer;

public class ConsoleJsonDebugRenderer implements DebugRenderer {
    private final Gson gson = new Gson();
    private final boolean shouldPrint;

    public ConsoleJsonDebugRenderer() {
        shouldPrint = Settings.getInstance().getBoolean(SettingKey.CONFIG_DEBUG_ENABLED);
    }

    @Override
    public void debug(String message) {
        if (shouldPrint) {
            StackTraceElement caller = getCaller();
            if (caller != null) {
                System.out.println(gson.toJson(new LogInfo(message, caller)));
            } else {
                System.out.println(gson.toJson(new LogInfo(message)));
            }
        }
    }

    @Override
    public void debug(String message, Exception e) {
        if (shouldPrint) {
            StackTraceElement caller = getCaller();
            if (caller != null) {
                System.out.println(gson.toJson(new LogInfo(message, e, caller)));
            } else {
                System.out.println(gson.toJson(new LogInfo(message, e)));
            }
        }
    }

    @Override
    public void debugf(String format, Object... args) {
        if (shouldPrint) {
            String message = String.format(format, args);
            StackTraceElement caller = getCaller();
            if (caller != null) {
                System.out.println(gson.toJson(new LogInfo(message, caller)));
            } else {
                System.out.println(gson.toJson(new LogInfo(message)));
            }
        }
    }

    private StackTraceElement getCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            return stackTrace[3];
        }
        return null;
    }

    class LogInfo {
        private String message;
        private String caller;
        private String error;

        @SerializedName(value = "stack_trace")
        private String[] stackTrace;

        public LogInfo(String message) {
            this.message = message;
        }

        public LogInfo(String message, Exception error) {
            this.message = message;
            this.error = error.toString();
            StackTraceElement[] stackTrace = error.getStackTrace();
            this.stackTrace = new String[stackTrace.length];
            for (int i = 0; i < stackTrace.length; i++) {
                this.stackTrace[i] = stackTrace[i].toString();
            }
        }

        public LogInfo(String message, StackTraceElement caller) {
            this(message);
            this.caller = caller.toString();
        }

        public LogInfo(String message, Exception error, StackTraceElement caller) {
            this(message, error);
            this.caller = caller.toString();
        }
    }
}
