package games.negative.survival;

import games.negative.alumina.AluminaPlugin;
import games.negative.survival.listener.CropListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmokingRecipe;
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

        SmokingRecipe rottenFleshToLeather = new SmokingRecipe(
                new NamespacedKey(this, "rotten_flesh_to_leather"),
                new ItemStack(Material.LEATHER),
                Material.ROTTEN_FLESH,
                1,
                100
        );

        Bukkit.getServer().addRecipe(rottenFleshToLeather);
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
