package parade.logger.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import parade.logger.Logger;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class JsonLogger extends Logger {
    private final Gson gson =
            new GsonBuilder()
                    .disableHtmlEscaping()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // ISO 8601 format
                    .create();

    public JsonLogger(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public JsonLogger(PrintWriter printWriter) {
        super(printWriter);
    }

    public JsonLogger(OutputStream out) {
        super(out);
    }

    public JsonLogger(Writer writer) {
        super(writer);
    }

    @Override
    public void log(String message) {
        writelnFlush(gson.toJson(new LogInfo(message)));
    }

    @Override
    public void log(String message, Exception e) {
        writelnFlush(gson.toJson(new LogInfo(message, e)));
    }

    @Override
    public void logf(String format, Object... args) {
        String message = String.format(format, args);
        writelnFlush(gson.toJson(new LogInfo(message)));
    }
}
