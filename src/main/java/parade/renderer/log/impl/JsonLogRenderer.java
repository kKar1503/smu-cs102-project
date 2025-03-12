package parade.renderer.log.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import parade.renderer.log.LogRenderer;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class JsonLogRenderer extends LogRenderer {
    private final Gson gson =
            new GsonBuilder()
                    .disableHtmlEscaping()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // ISO 8601 format
                    .create();

    public JsonLogRenderer(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public JsonLogRenderer(PrintWriter printWriter) {
        super(printWriter);
    }

    public JsonLogRenderer(OutputStream out) {
        super(out);
    }

    public JsonLogRenderer(Writer writer) {
        super(writer);
    }

    @Override
    public void debug(String message) {
        writelnFlush(gson.toJson(new LogInfo(message)));
    }

    @Override
    public void debug(String message, Exception e) {
        writelnFlush(gson.toJson(new LogInfo(message, e)));
    }

    @Override
    public void debugf(String format, Object... args) {
        String message = String.format(format, args);
        writelnFlush(gson.toJson(new LogInfo(message)));
    }
}
