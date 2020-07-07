package me.MathiasMC.PvPBuilder.managers;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CalculateManager {

    private final PvPBuilder plugin;

    public CalculateManager(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    public Location getCalculatedBlockLocation(int blockX, int blockY, int blockZ, World world, String direction, String[] split) {
        if (direction != null) {
            if (direction.equalsIgnoreCase("N")) {
                return new Location(world, (blockX + Integer.parseInt(split[2]) - Integer.parseInt(split[5])), (blockY + Integer.parseInt(split[1]) - Integer.parseInt(split[4])), (blockZ - Integer.parseInt(split[0]) + Integer.parseInt(split[3])));
            } else if (direction.equalsIgnoreCase("W")) {
                return new Location(world, (blockX - Integer.parseInt(split[0]) + Integer.parseInt(split[3])), (blockY + Integer.parseInt(split[1]) - Integer.parseInt(split[4])), (blockZ - Integer.parseInt(split[2]) + Integer.parseInt(split[5])));
            } else if (direction.equalsIgnoreCase("S")) {
                return new Location(world, (blockX - Integer.parseInt(split[2]) + Integer.parseInt(split[5])), (blockY + Integer.parseInt(split[1]) - Integer.parseInt(split[4])), (blockZ + Integer.parseInt(split[0]) - Integer.parseInt(split[3])));
            } else if (direction.equalsIgnoreCase("E")) {
                return new Location(world, (blockX + Integer.parseInt(split[0]) - Integer.parseInt(split[3])), (blockY + Integer.parseInt(split[1]) - Integer.parseInt(split[4])), (blockZ + Integer.parseInt(split[2]) - Integer.parseInt(split[5])));
            }
        }
        return null;
    }

    public String getDirection(Player player) {
        double yaw = ((player.getLocation().getYaw() - 90.0F) % 360.0F);
        if (yaw < 0.0D)
            yaw += 360.0D;
        if (0.0D <= yaw && yaw < 22.5D)
            return "N";
        if (22.5D <= yaw && yaw < 67.5D)
            return "N";
        if (67.5D <= yaw && yaw < 112.5D)
            return "E";
        if (112.5D <= yaw && yaw < 157.5D)
            return "E";
        if (157.5D <= yaw && yaw < 202.5D)
            return "S";
        if (202.5D <= yaw && yaw < 247.5D)
            return "W";
        if (247.5D <= yaw && yaw < 292.5D)
            return "W";
        if (292.5D <= yaw && yaw < 337.5D)
            return "N";
        if (337.5D <= yaw && yaw < 360.0D)
            return "N";
        return null;
    }
}