package parade.textrenderer.impl;

import parade.textrenderer.ConsoleColors;
import parade.textrenderer.DebugRenderer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleDebugRenderer implements DebugRenderer {
    public ConsoleDebugRenderer() {}

    @Override
    public void debug(String message) {
        print(message);
    }

    @Override
    public void debug(String message, Exception e) {
        print(message);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // Split stack trace into lines and format each line
        StringBuilder formattedTrace = new StringBuilder();
        String[] lines = stackTrace.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (i == 0) {
                formattedTrace
                        .append(ConsoleColors.RED)
                        .append(lines[i])
                        .append(ConsoleColors.RESET)
                        .append("\n"); // Exception message in red
            } else {
                formattedTrace
                        .append("    ")
                        .append(ConsoleColors.GREY)
                        .append(lines[i])
                        .append(ConsoleColors.RESET)
                        .append("\n"); // Stack trace in grey with indentation
            }
        }
        System.out.println(formattedTrace);
    }

    @Override
    public void debugf(String format, Object... args) {
            print(String.format(format, args));
    }

    private void print(String message) {
        System.out.println(
                getTimestamp()
                        + " "
                        + getCaller()
                        + ConsoleColors.CYAN
                        + " > "
                        + ConsoleColors.RESET
                        + message);
    }

    private String getCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 4) {
            return stackTrace[4].toString();
        }
        return "Unknown caller";
    }

    private String getTimestamp() {
        return ConsoleColors.GREY
                + new SimpleDateFormat("HH:mma").format(new Date()).toUpperCase()
                + ConsoleColors.RESET;
    }
}
