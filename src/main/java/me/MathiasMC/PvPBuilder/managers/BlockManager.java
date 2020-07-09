package me.MathiasMC.PvPBuilder.managers;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Ladder;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;

import java.util.List;
import java.util.Random;

public class BlockManager {

    private final PvPBuilder plugin;

    public BlockManager(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    public void setBlocks(final Player player, Location location, String fileName) {
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();
        World world = location.getWorld();
        String direction = plugin.calculateManager.getDirection(player);
        FileConfiguration fileConfiguration = plugin.blocksFileConfiguration.get(fileName);
        for (int i = 0; i < plugin.systemManager.getBlocks(fileName).size(); i++) {
            String[] split = fileConfiguration.getString(plugin.systemManager.getBlocks(fileName).get(i) + ".options").split(" ");
            Location currentLocation = plugin.calculateManager.getCalculatedBlockLocation(blockX, blockY, blockZ, world, direction, split);
            if (currentLocation != null) {
                setLocationBlock(currentLocation, split, direction, plugin.itemStackArray.get(fileName).get(i), player, fileConfiguration, plugin.systemManager.getBlocks(fileName).get(i));
            }
        }
        if (fileConfiguration.contains("area")) {
            String areaGroup = plugin.areaManager.getAreaGroup(player, fileConfiguration);
            if (areaGroup != null) {
                Location start = plugin.calculateManager.getCalculatedBlockLocation(blockX, blockY, blockZ, player.getWorld(), direction, fileConfiguration.getString("area.start").split(" "));
                Location end = plugin.calculateManager.getCalculatedBlockLocation(blockX, blockY, blockZ, player.getWorld(), direction, fileConfiguration.getString("area.end").split(" "));
                plugin.areaManager.run(player, fileConfiguration, areaGroup, start, end);
            }
        }
    }

    private void setLocationBlock(Location locationBlock, String[] split, String direction, ItemStack itemStack, Player player, FileConfiguration fileConfiguration, String currentGroup) {
        if (split[6].equalsIgnoreCase("false")) {
            if (locationBlock.getBlock().getType().equals(Material.AIR)) {
                if (Integer.parseInt(split[7]) > 0) {
                    scheduleTime(locationBlock, split, direction, itemStack, player, fileConfiguration, currentGroup);
                } else {
                    setBlock(locationBlock.getBlock(), itemStack, player, fileConfiguration, currentGroup);
                    removeBlocks(split, locationBlock, player, fileConfiguration, currentGroup);
                    setState(locationBlock, split, direction);
                }
            }
        } else if (Integer.parseInt(split[7]) > 0) {
            scheduleTime(locationBlock, split, direction, itemStack, player, fileConfiguration, currentGroup);
        } else {
            setBlock(locationBlock.getBlock(), itemStack, player, fileConfiguration, currentGroup);
            removeBlocks(split, locationBlock, player, fileConfiguration, currentGroup);
            setState(locationBlock, split, direction);
        }
    }

    private void scheduleTime(final Location location, final String[] split, final String direction, final ItemStack itemStack, final Player player, final FileConfiguration fileConfiguration, final String currentGroup) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            setBlock(location.getBlock(), itemStack, player, fileConfiguration, currentGroup);
            removeBlocks(split, location, player, fileConfiguration, currentGroup);
            setState(location, split, direction);
        }, Long.parseLong(split[7]));
    }

    private void removeBlocks(String[] split, final Location location, final Player player, final FileConfiguration fileConfiguration, final String currentGroup) {
        if (Integer.parseInt(split[8]) > 0) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () ->{
                location.getBlock().setType(Material.AIR);
                dispatchBlockCommands(fileConfiguration, player, currentGroup, location, "remove");
            }, Long.parseLong(split[8]));
        }
    }

    private void setBlock(final Block block, final ItemStack itemStack, final Player player, final FileConfiguration fileConfiguration, final String currentGroup) {
        block.setType(itemStack.getType());
        if (block.getType().equals(itemStack.getType())) {
            block.getState().setData(itemStack.getData());
        }
        dispatchBlockCommands(fileConfiguration, player, currentGroup, block.getLocation(), "set");
    }

    private void dispatchBlockCommands(final FileConfiguration fileConfiguration, final Player player, final String currentGroup, final Location location, final String type) {
        if (fileConfiguration.contains(currentGroup + ".commands." + type + ".commands")) {
            for (String command : fileConfiguration.getStringList(currentGroup + ".commands." + type + ".commands")) {
                dispatchCommands(player, command, location);
            }
        }
        if (fileConfiguration.contains(currentGroup + ".commands." + type + ".random-commands")) {
            List<String> list = fileConfiguration.getStringList(currentGroup + ".commands." + type + ".random-commands.list");
            for (int i = 0; i < fileConfiguration.getInt(currentGroup + ".commands." + type + ".random-commands.amount"); i++) {
                dispatchCommands(player, list.get(randomCommand(list)), location);
            }
        }
    }

    private void dispatchCommands(Player player, String command, Location location) {
        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{pvpbuilder_player}", player.getName()).replace("{pvpbuilder_world}", location.getWorld().getName()).replace("{pvpbuilder_x}", String.valueOf(location.getBlockX())).replace("{pvpbuilder_y}", String.valueOf(location.getBlockY())).replace("{pvpbuilder_z}", String.valueOf(location.getBlockZ())));
    }

    private int randomCommand(List<String> list) {
        Random random = new Random();
        return random.nextInt(list.size());
    }

    private void setState(Location location, String[] split, String direction) {
        if (direction.equalsIgnoreCase("N")) {
            if (Integer.parseInt(split[9]) == 1) {
                setFace(location, BlockFace.WEST);
            } else if (Integer.parseInt(split[9]) == 2) {
                setFace(location, BlockFace.NORTH);
            } else if (Integer.parseInt(split[9]) == 3) {
                setFace(location, BlockFace.SOUTH);
            } else if (Integer.parseInt(split[9]) == 4) {
                setFace(location, BlockFace.EAST);
            }
        } else if (direction.equalsIgnoreCase("W")) {
            if (Integer.parseInt(split[9]) == 1) {
                setFace(location, BlockFace.SOUTH);
            } else if (Integer.parseInt(split[9]) == 2) {
                setFace(location, BlockFace.WEST);
            } else if (Integer.parseInt(split[9]) == 3) {
                setFace(location, BlockFace.EAST);
            } else if (Integer.parseInt(split[9]) == 4) {
                setFace(location, BlockFace.NORTH);
            }
        } else if (direction.equalsIgnoreCase("S")) {
            if (Integer.parseInt(split[9]) == 1) {
                setFace(location, BlockFace.EAST);
            } else if (Integer.parseInt(split[9]) == 2) {
                setFace(location, BlockFace.SOUTH);
            } else if (Integer.parseInt(split[9]) == 3) {
                setFace(location, BlockFace.NORTH);
            } else if (Integer.parseInt(split[9]) == 4) {
                setFace(location, BlockFace.WEST);
            }
        } else if (direction.equalsIgnoreCase("E")) {
            if (Integer.parseInt(split[9]) == 1) {
                setFace(location, BlockFace.NORTH);
            } else if (Integer.parseInt(split[9]) == 2) {
                setFace(location, BlockFace.EAST);
            } else if (Integer.parseInt(split[9]) == 3) {
                setFace(location, BlockFace.WEST);
            } else if (Integer.parseInt(split[9]) == 4) {
                setFace(location, BlockFace.SOUTH);
            }
        }
    }

    private void setFace(Location location, BlockFace face) {
        BlockState state = location.getBlock().getState();
        MaterialData data = state.getData();
        if (data instanceof Stairs) {
            Stairs stair = (Stairs)data;
            stair.setFacingDirection(face);
            state.setData(stair);
            state.update(false, false);
        } else if (data instanceof Ladder) {
            Ladder ladder = (Ladder)data;
            ladder.setFacingDirection(face);
            state.setData(ladder);
            state.update(true, true);
        }
    }
}