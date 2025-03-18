package parade.logger;

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
}
