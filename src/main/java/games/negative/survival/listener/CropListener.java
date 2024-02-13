package games.negative.survival.listener;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import games.negative.alumina.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class CropListener implements Listener {

    @EventHandler
    public void onTrample(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.PHYSICAL))) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Material type = clickedBlock.getType();
        if (type != Material.FARMLAND) return;

        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
    }

    @EventHandler
    public void onHarvest(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Check if holding a hoe of any type
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(item.getType().name().toUpperCase().contains("HOE"))) return;

        Block block = event.getBlock();
        BlockData data = block.getBlockData();

        // Check if the block is a crop
        if (!(data instanceof Ageable crop)) {
            Bukkit.broadcastMessage("Not a crop");
            return;
        }

        event.setCancelled(true);

        // If age is not the maximum age, do not break the crop or distribute the drops
        if (crop.getAge() != crop.getMaximumAge()) return;

        // Drop the crop
        Collection<ItemStack> drops = block.getDrops(item);

        // If the player's inventory is full, drop the items on the ground
        Collection<ItemStack> failed = fillInventory(player, drops);
        Location location = block.getLocation();
        World world = block.getWorld();
        for (ItemStack drop : failed) {
            world.dropItemNaturally(location, drop);
        }

        // Set the crop to the last age
        crop.setAge(0);
        block.setBlockData(crop);
    }

    /**
     * Fills the player's inventory with the given collection of items.
     * @param player The player to fill the inventory with.
     * @param collection The collection of items to fill the inventory with.
     * @return A collection of items that failed to be added to the player's inventory.
     * @param <T> The type of the collection.
     */
    private <T extends Iterable<ItemStack>> Collection<ItemStack> fillInventory(@NotNull Player player, @NotNull T collection) {
        Preconditions.checkNotNull(player, "'player' cannot be null!");
        Preconditions.checkNotNull(collection, "'collection' cannot be null!");

        List<ItemStack> failed = Lists.newArrayList();

        PlayerInventory inv = player.getInventory();
        for (ItemStack item : collection) {
            // Make sure player's inventory isn't full
            if (inv.firstEmpty() != -1) {
                inv.addItem(item);
                continue;
            }

            int amount = item.getAmount();

            for (ItemStack content : inv.getContents()) {
                if (content == null || !content.isSimilar(item)) continue;

                int max = content.getMaxStackSize();
                int current = content.getAmount();

                int difference = Math.abs(max - current);
                if (amount > difference) {
                    content.setAmount(max);
                    amount -= difference;
                    continue;
                }

                content.setAmount(current + amount);
                amount = 0;
            }

            if (amount <= 0) continue;

            item.setAmount(amount);
            failed.add(item);
        }

        player.updateInventory();
        return failed;
    }
}
