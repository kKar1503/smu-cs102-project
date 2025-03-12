package parade.logger;

public class LoggerProvider {
    private static Logger instance = null;

    public static void setInstance(Logger logger) {
        instance = logger;
    }

    /**
     * Get the instance of the Logger.
     *
     * @return the instance of the Logger that has been set to this provider
     * @throws IllegalStateException if the instance is not yet set
     */
    public static Logger getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("Logger is not yet set");
        }
        return instance;
    }
}
