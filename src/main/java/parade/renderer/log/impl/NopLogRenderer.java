package parade.renderer.log.impl;

import parade.renderer.log.LogRenderer;

/**
 * A no-operation (NOP) implementation of the DebugRenderer interface. This class is used when
 * debugging is disabled or not needed. It does not perform any logging or output.
 */
public class NopLogRenderer extends LogRenderer {
    public NopLogRenderer() {}

    @Override
    public void debug(String message) {}

    @Override
    public void debug(String message, Exception e) {}

    @Override
    public void debugf(String format, Object... args) {}
}
