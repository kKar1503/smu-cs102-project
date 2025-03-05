package parade.textrenderer.impl;

import parade.settings.SettingKey;
import parade.settings.Settings;
import parade.textrenderer.DebugRenderer;

public class BasicDebugRenderer implements DebugRenderer {
    private final boolean shouldPrint;

    public BasicDebugRenderer() {
        shouldPrint = Settings.getInstance().getBoolean(SettingKey.CONFIG_DEBUG);
    }

    @Override
    public void debug(String message) {
        if (shouldPrint) {
            System.out.println(message);
        }
    }

    @Override
    public void debug(String message, Throwable t) {
        if (shouldPrint) {
            System.out.println(message);
            t.printStackTrace();
        }
    }

    @Override
    public void debug(String format, Object... args) {
        if (shouldPrint) {
            System.out.println(format);
            for (Object arg : args) {
                System.out.println("\t" + arg.toString());
            }
        }
    }

    @Override
    public void debugf(String format, Object... args) {
        if (shouldPrint) {
            System.out.printf(format, args);
        }
    }
}
