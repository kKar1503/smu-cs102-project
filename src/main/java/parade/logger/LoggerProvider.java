package parade.logger;

import parade.logger.impl.*;
import parade.setting.Setting;
import parade.setting.SettingKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class LoggerProvider {
    private static AbstractLogger instance = null;

    public static void setInstance(AbstractLogger logger) {
        instance = logger;
    }

    /**
     * Get the instance of the Logger.
     *
     * @return the instance of the Logger that has been set to this provider
     * @throws IllegalStateException if the instance is not yet set
     */
    public static AbstractLogger getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("Logger is not yet set");
        }
        return instance;
    }

    public static void setupLogger() throws IOException {
        Setting settings = Setting.get();

        String loggerTypes = settings.get(SettingKey.LOGGER_TYPES);
        boolean shouldLog = settings.getBoolean(SettingKey.LOGGER_ENABLED);
        AbstractLogger logger;
        if (!shouldLog) {
            logger = new NopLogger();
        } else {
            String[] loggerTypesArr = loggerTypes.split(",");
            // Handles duplicate logger types and trim whitespace
            List<String> loggerTypesArrNoDuplicates =
                    Stream.of(loggerTypesArr).map(String::trim).distinct().toList();
            if (loggerTypesArrNoDuplicates.size() == 1) {
                logger = determineLoggerType(loggerTypesArrNoDuplicates.getFirst());
            } else {
                AbstractLogger[] loggers = new AbstractLogger[loggerTypesArrNoDuplicates.size()];
                int i = 0;
                for (String loggerType : loggerTypesArrNoDuplicates) {
                    loggers[i++] = determineLoggerType(loggerType);
                }
                logger = new MultiLogger(loggers);
            }
        }
        LoggerProvider.setInstance(logger);
    }

    private static AbstractLogger determineLoggerType(String loggerType)
            throws IllegalStateException, IOException {
        return switch (loggerType) {
            case "console" -> new PrettyLogger();
            case "console_json" -> new JsonLogger(System.out);
            case "file_json" -> {
                String filePath = Setting.get().get(SettingKey.LOGGER_FILE);
                if (filePath == null || filePath.isEmpty()) {
                    throw new IllegalStateException("File path for logger is not set in settings");
                }
                // Creates the directory if it does not exist
                Path path = Path.of(filePath);
                Path parentDir = path.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                yield new JsonLogger(filePath);
            }
            default ->
                    throw new IllegalStateException(
                            "Unknown logger type in settings: " + loggerType);
        };
    }
}
