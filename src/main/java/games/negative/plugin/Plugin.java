package games.negative.plugin;

import games.negative.alumina.AluminaPlugin;
import org.jetbrains.annotations.NotNull;

public class Plugin extends AluminaPlugin {

    private static Plugin instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    /**
     * Returns the instance of the plugin.
     * @return The instance of the plugin.
     */
    @NotNull
    public static Plugin instance() {
        return instance;
    }
}
