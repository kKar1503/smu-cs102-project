package parade.logger.impl;

import parade.logger.Logger;

/**
 * MultiLogger is a wrapper class that allows for multiple loggers to be used together. It
 * implements the Logger interface and delegates the log messages to all registered loggers.
 */
public class MultiLogger extends Logger {
    Logger[] delegates;

    public MultiLogger(Logger... delegates) {
        this.delegates = delegates;
    }

    @Override
    public void log(String message) {
        for (Logger delegate : delegates) {
            delegate.log(message);
        }
    }

    @Override
    public void log(String message, Exception e) {
        for (Logger delegate : delegates) {
            delegate.log(message, e);
        }
    }

    @Override
    public void logf(String format, Object... args) {
        for (Logger delegate : delegates) {
            delegate.logf(format, args);
        }
    }
}
