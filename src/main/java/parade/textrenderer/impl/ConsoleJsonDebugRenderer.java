package parade.textrenderer.impl;

import com.google.gson.Gson;

import parade.textrenderer.DebugRenderer;
import parade.textrenderer.LogInfo;

public class ConsoleJsonDebugRenderer implements DebugRenderer {
    private final Gson gson = new Gson();

    public ConsoleJsonDebugRenderer() {}

    @Override
    public void debug(String message) {
        StackTraceElement caller = getCaller();
        if (caller != null) {
            System.out.println(gson.toJson(new LogInfo(message, caller)));
        } else {
            System.out.println(gson.toJson(new LogInfo(message)));
        }
    }

    @Override
    public void debug(String message, Exception e) {
        StackTraceElement caller = getCaller();
        if (caller != null) {
            System.out.println(gson.toJson(new LogInfo(message, e, caller)));
        } else {
            System.out.println(gson.toJson(new LogInfo(message, e)));
        }
    }

    @Override
    public void debugf(String format, Object... args) {
        String message = String.format(format, args);
        StackTraceElement caller = getCaller();
        if (caller != null) {
            System.out.println(gson.toJson(new LogInfo(message, caller)));
        } else {
            System.out.println(gson.toJson(new LogInfo(message)));
        }
    }

    private StackTraceElement getCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length > 3 ? stackTrace[3] : null;
    }
}
