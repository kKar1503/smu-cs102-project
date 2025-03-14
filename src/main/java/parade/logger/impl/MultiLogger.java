package parade.logger.impl;

import parade.logger.AbstractLogger;

/**
 * MultiLogger is a wrapper class that allows for multiple loggers to be used together. It
 * implements the Logger interface and delegates the log messages to all registered loggers.
 */
public class MultiLogger extends AbstractLogger {
    AbstractLogger[] delegates;

    public MultiLogger(AbstractLogger... delegates) {
        this.delegates = delegates;
    }

    @Override
    public void log(String message) {
        for (AbstractLogger delegate : delegates) {
            delegate.log(message);
        }
    }

    @Override
    public void log(String message, Exception e) {
        for (AbstractLogger delegate : delegates) {
            delegate.log(message, e);
        }
    }

    @Override
    public void logf(String format, Object... args) {
        for (AbstractLogger delegate : delegates) {
            delegate.logf(format, args);
        }
    }

    @Override
    public void close() {
        for (AbstractLogger delegate : delegates) {
            delegate.close();
        }
    }
}
