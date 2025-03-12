package parade.renderer.debug.impl;

import parade.renderer.debug.DebugRenderer;

/**
 * MultiDebugRenderer is a wrapper class that allows for multiple debug renderers to be used
 * together. It implements the DebugRenderer interface and delegates the debug messages to all
 * registered renderers.
 */
public class MultiDebugRenderer implements DebugRenderer {
    DebugRenderer[] delegates;

    public MultiDebugRenderer(DebugRenderer... delegates) {
        this.delegates = delegates;
    }

    @Override
    public void debug(String message) {
        for (DebugRenderer delegate : delegates) {
            delegate.debug(message);
        }
    }

    @Override
    public void debug(String message, Exception e) {
        for (DebugRenderer delegate : delegates) {
            delegate.debug(message, e);
        }
    }

    @Override
    public void debugf(String format, Object... args) {
        for (DebugRenderer delegate : delegates) {
            delegate.debugf(format, args);
        }
    }
}
