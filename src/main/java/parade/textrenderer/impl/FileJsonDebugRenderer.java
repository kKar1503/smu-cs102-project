package parade.textrenderer.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import parade.textrenderer.DebugRenderer;
import parade.textrenderer.LogInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileJsonDebugRenderer implements DebugRenderer {
    private static final String LOG_FILE_PATH = "logs/debug.log";

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final boolean shouldPrint;
    private final BufferedWriter writer;

    public FileJsonDebugRenderer() throws IOException {
        this(LOG_FILE_PATH, true);
    }

    public FileJsonDebugRenderer(String logFilePath, boolean shouldPrint) throws IOException {
        this.shouldPrint = shouldPrint;
        ensureLogFileExists(logFilePath);
        writer = new BufferedWriter(new FileWriter(logFilePath, true));

        // Add a shutdown hook to close the writer when the application exits
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void debug(String message) {
        if (shouldPrint) {
            StackTraceElement caller = getCaller();
            if (caller != null) {
                writeLogToFile(new LogInfo(message, caller));
            } else {
                writeLogToFile(new LogInfo(message));
            }
        }
    }

    @Override
    public void debug(String message, Exception e) {
        if (shouldPrint) {
            StackTraceElement caller = getCaller();
            if (caller != null) {
                writeLogToFile(new LogInfo(message, e, caller));
            } else {
                writeLogToFile(new LogInfo(message, e));
            }
        }
    }

    @Override
    public void debugf(String format, Object... args) {
        if (shouldPrint) {
            String message = String.format(format, args);
            StackTraceElement caller = getCaller();
            if (caller != null) {
                writeLogToFile(new LogInfo(message, caller));
            } else {
                writeLogToFile(new LogInfo(message));
            }
        }
    }

    private StackTraceElement getCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length > 3 ? stackTrace[3] : null;
    }

    private void writeLogToFile(LogInfo logInfo) {
        if (writer != null) {
            try {
                writer.write(gson.toJson(logInfo));
                writer.newLine();
                writer.flush(); // Ensure the log is written immediately
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void ensureLogFileExists(String logFilePath) throws IOException {
        Path path = Path.of(logFilePath);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        if (!new java.io.File(logFilePath).exists()) {
            new java.io.File(logFilePath).createNewFile();
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
