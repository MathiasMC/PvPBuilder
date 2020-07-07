package me.MathiasMC.PvPBuilder.listeners;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlace implements Listener {

    private final PvPBuilder plugin;

    public BlockPlace(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack;
        try {
            itemStack = player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError error) {
            itemStack = player.getInventory().getItemInHand();
        }
        itemStack = plugin.getItemStack(itemStack);
        if (plugin.handItemStack.containsKey(itemStack)) {
            String name = plugin.handItemStack.get(itemStack);
            if (player.hasPermission("pvpbuilder.place." + name)) {
                FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(name);
                if (plugin.systemManager.world(player, fileConfiguration, "")) {
                    if (fileConfiguration.contains("zone")) {
                        String[] loc1 = fileConfiguration.getString("zone.start").split(" ");
                        String[] loc2 = fileConfiguration.getString("zone.end").split(" ");
                        World world = plugin.getServer().getWorld(fileConfiguration.getString("zone.world"));
                        if (!plugin.systemManager.isInLocation(e.getBlock().getLocation(), new Location(world, Integer.parseInt(loc1[0]), Integer.parseInt(loc1[1]), Integer.parseInt(loc1[2])), new Location(world, Integer.parseInt(loc2[0]), Integer.parseInt(loc2[1]), Integer.parseInt(loc2[2])))) {
                            return;
                        }
                    }
                    plugin.blockManager.setBlocks(player, e.getBlockPlaced().getLocation(), name);
                }
            }
        }
    }
}