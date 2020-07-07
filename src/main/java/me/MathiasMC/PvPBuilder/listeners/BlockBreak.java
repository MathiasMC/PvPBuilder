package me.MathiasMC.PvPBuilder.listeners;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BlockBreak implements Listener {

    private final PvPBuilder plugin;

    public BlockBreak(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlock(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack;
        try {
            itemStack = player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError error) {
            itemStack = player.getInventory().getItemInHand();
        }
        if (itemStack.equals(plugin.wand)) {
            if (player.hasPermission("pvpbuilder.wand.use")) {
                String uuid = player.getUniqueId().toString();
                if (!plugin.pos1.containsKey(uuid)) {
                    Location location = e.getBlock().getLocation();
                    plugin.pos1.put(uuid, location);
                    for (String message : plugin.language.get.getStringList("player.pvpbuilder.wand.set.1")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_x}", String.valueOf(location.getBlockX())).replace("{pvpbuilder_y}", String.valueOf(location.getBlockY())).replace("{pvpbuilder_z}", String.valueOf(location.getBlockZ()))));
                    }
                } else {
                    Location location = e.getBlock().getLocation();
                    if (!plugin.pos1.get(player.getUniqueId().toString()).equals(location)) {
                        if (!plugin.pos2.containsKey(player.getUniqueId().toString())) {
                            plugin.pos2.put(player.getUniqueId().toString(), location);
                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.wand.set.2")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{pvpbuilder_x}", String.valueOf(location.getBlockX())).replace("{pvpbuilder_y}", String.valueOf(location.getBlockY())).replace("{pvpbuilder_z}", String.valueOf(location.getBlockZ()))));
                            }
                        } else {
                            plugin.pos1.remove(player.getUniqueId().toString());
                            plugin.pos2.remove(player.getUniqueId().toString());
                            for (String message : plugin.language.get.getStringList("player.pvpbuilder.wand.set.clear")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (String message : plugin.language.get.getStringList("player.pvpbuilder.wand.set.same")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                }
            } else {
                for (String message : plugin.language.get.getStringList("player.pvpbuilder.wand.set.permission")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
            e.setCancelled(true);
        }
    }
}
