package parade.renderer.debug.impl;

import parade.renderer.debug.DebugRenderer;

/**
 * A no-operation (NOP) implementation of the DebugRenderer interface. This class is used when
 * debugging is disabled or not needed. It does not perform any logging or output.
 */
public class NopDebugRenderer implements DebugRenderer {
    public NopDebugRenderer() {}

    @Override
    public void debug(String message) {}

    @Override
    public void debug(String message, Exception e) {}

    @Override
    public void debugf(String format, Object... args) {}
}
