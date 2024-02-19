package games.negative.survival;

import games.negative.alumina.AluminaPlugin;
import games.negative.alumina.builder.ItemBuilder;
import games.negative.alumina.event.Events;
import games.negative.alumina.util.NBTEditor;
import games.negative.survival.core.Keys;
import games.negative.survival.listener.CropListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
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
                Keys.ROTTEN_FLESH_TO_LEATHER,
                new ItemStack(Material.LEATHER),
                Material.ROTTEN_FLESH,
                1,
                100
        );

        Bukkit.getServer().addRecipe(rottenFleshToLeather);

        ShapedRecipe virtualCraftingTable = new ShapedRecipe(
                Keys.VIRTUAL_CRAFTING_TABLE,
                new ItemBuilder(Material.CRAFTING_TABLE)
                        .setName("&aVirtual Crafting Table &7(Right Click)")
                        .applyPersistentData(data -> data.set(Keys.VIRTUAL_CRAFTING_TABLE, PersistentDataType.BOOLEAN, true))
                        .build()
        );

        virtualCraftingTable.shape("LCL");
        virtualCraftingTable.setIngredient('L', Material.LEATHER);
        virtualCraftingTable.setIngredient('C', Material.CRAFTING_TABLE);

        Bukkit.getServer().addRecipe(virtualCraftingTable);

        Events.listen(PlayerInteractEvent.class, event -> {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            if (item == null || item.getType() != Material.CRAFTING_TABLE) return;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            if (!NBTEditor.has(meta, Keys.VIRTUAL_CRAFTING_TABLE)) return;

            event.setCancelled(true);
            event.setUseInteractedBlock(PlayerInteractEvent.Result.DENY);
            event.setUseItemInHand(PlayerInteractEvent.Result.DENY);

            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            player.openWorkbench(null, true);
        });
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
