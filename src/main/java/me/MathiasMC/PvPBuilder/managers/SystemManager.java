package me.MathiasMC.PvPBuilder.managers;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SystemManager {

    private final PvPBuilder plugin;

    public SystemManager(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    public void convertItemStacks(final String fileName) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(fileName);
        if (fileConfiguration != null) {
            for (String key : getBlocks(fileName)) {
                itemStacks.add(plugin.getID(fileConfiguration.getString(key + ".material"), 1));
            }
            if (fileConfiguration.getConfigurationSection("hand") != null) {
                plugin.handItemStack.put(ItemStack.deserialize(fileConfiguration.getConfigurationSection("hand").getValues(false)), fileName);
            }
        }
        plugin.itemStackArray.put(fileName, itemStacks);
    }

    public ArrayList<String> getBlocks(final String fileName) {
        ArrayList<String> itemStacks = new ArrayList<>();
        FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(fileName);
        if (fileConfiguration != null) {
            for (String key : fileConfiguration.getConfigurationSection("").getKeys(false)) {
                if (!key.equalsIgnoreCase("worlds") && !key.equalsIgnoreCase("hand") && !key.equalsIgnoreCase("zone") && !key.equalsIgnoreCase("area")) {
                    itemStacks.add(key);
                }
            }
        }
        return itemStacks;
    }

    public boolean isInLocation(Location block, Location start, Location end) {
        Location loc1 = new Location(block.getWorld(), Math.min(start.getBlockX(), end.getBlockX()), Math.min(start.getBlockY(), end.getBlockY()), Math.min(start.getBlockZ(), end.getBlockZ()));
        Location loc2 = new Location(block.getWorld(), Math.max(start.getBlockX(), end.getBlockX()), Math.max(start.getBlockY(), end.getBlockY()), Math.max(start.getBlockZ(), end.getBlockZ()));
        if (block.getBlockX() >= loc1.getBlockX() && block.getBlockX() <= loc2.getBlockX() && block.getBlockY() >= loc1.getBlockY() && block.getBlockY() <= loc2.getBlockY() && block.getBlockZ() >= loc1.getBlockZ() && block.getBlockZ() <= loc2.getBlockZ()) {
            return true;
        }
        return false;
    }

    public boolean world(Player player, FileConfiguration fileConfiguration, String path) {
        if (fileConfiguration.contains(path + ".worlds")) {
            List<String> worlds = fileConfiguration.getStringList(path + ".worlds");
            return worlds.contains(player.getWorld().getName());
        }
        return true;
    }
}