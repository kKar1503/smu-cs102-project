package parade.renderer.log.impl;

import parade.renderer.log.LogRenderer;

/**
 * MultiDebugRenderer is a wrapper class that allows for multiple debug renderers to be used
 * together. It implements the DebugRenderer interface and delegates the debug messages to all
 * registered renderers.
 */
public class MultiLogRenderer extends LogRenderer {
    LogRenderer[] delegates;

    public MultiLogRenderer(LogRenderer... delegates) {
        this.delegates = delegates;
    }

    @Override
    public void debug(String message) {
        for (LogRenderer delegate : delegates) {
            delegate.debug(message);
        }
    }

    @Override
    public void debug(String message, Exception e) {
        for (LogRenderer delegate : delegates) {
            delegate.debug(message, e);
        }
    }

    @Override
    public void debugf(String format, Object... args) {
        for (LogRenderer delegate : delegates) {
            delegate.debugf(format, args);
        }
    }
}
