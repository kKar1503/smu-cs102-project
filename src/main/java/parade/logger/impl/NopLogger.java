package parade.logger.impl;

import parade.logger.AbstractLogger;

/**
 * A no-operation (NOP) implementation of the Logger interface. This class is used when logging is
 * disabled or not needed. It does not perform any logging or output.
 */
public class NopLogger extends AbstractLogger {
    public NopLogger() {}

    @Override
    public void log(String message) {}

    @Override
    public void log(String message, Exception e) {}

    @Override
    public void logf(String format, Object... args) {}

    @Override
    public void close() {}
}
