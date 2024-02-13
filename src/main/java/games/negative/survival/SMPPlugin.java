package games.negative.survival;

import games.negative.alumina.AluminaPlugin;
import games.negative.survival.listener.CropListener;
import org.jetbrains.annotations.NotNull;

public class SMPPlugin extends AluminaPlugin {

    private static SMPPlugin instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        registerListeners(
                new CropListener()
        );
    }

    @Override
    public void disable() {

    }

    /**
     * Returns the instance of the plugin.
     * @return The instance of the plugin.
     */
    @NotNull
    public static SMPPlugin instance() {
        return instance;
    }
}
