package me.MathiasMC.PvPBuilder.managers;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AreaManager {

    private final PvPBuilder plugin;

    public AreaManager(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    public void run(final Player player, final FileConfiguration fileConfiguration, String group, Location start, Location end) {
        final int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            runCommandsArea(player, fileConfiguration, group, start, end, "area.groups." + group + ".commands");
        }, fileConfiguration.getLong("area.delay"), fileConfiguration.getLong("area.groups." + group + ".time"));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getServer().getScheduler().cancelTask(id);
            runCommandsArea(player, fileConfiguration, group, start, end, "area.groups." + group + ".commands-last");
        }, fileConfiguration.getLong("area.delay-disappear"));
    }

    public String getAreaGroup(Player player, FileConfiguration fileConfiguration) {
        String group = null;
        for (String currentGroup : fileConfiguration.getConfigurationSection("area.groups").getKeys(false)) {
            if (player.hasPermission("area.groups." + currentGroup + ".permission")) {
                group = currentGroup;
            }
        }
        return group;
    }

    private void runCommandsArea(Player player, FileConfiguration fileConfiguration, String group, Location start, Location end, String path) {
        if (fileConfiguration.getBoolean("area.groups." + group + ".all-players")) {
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                Location onlineLoc = online.getLocation();
                if (plugin.systemManager.isInLocation(onlineLoc, start, end)) {
                    for (String command : fileConfiguration.getStringList(path)) {
                        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{pvpbuilder_player}", online.getName()).replace("{pvpbuilder_x}", String.valueOf(onlineLoc.getBlockX())).replace("{pvpbuilder_y}", String.valueOf(onlineLoc.getBlockY())).replace("{pvpbuilder_z}", String.valueOf(onlineLoc.getBlockZ())));
                    }
                }
            }
        } else {
            Location playerLoc = player.getLocation();
            if (plugin.systemManager.isInLocation(playerLoc, start, end)) {
                for (String command : fileConfiguration.getStringList(path)) {
                    plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{pvpbuilder_player}", player.getName()).replace("{pvpbuilder_x}", String.valueOf(playerLoc.getBlockX())).replace("{pvpbuilder_y}", String.valueOf(playerLoc.getBlockY())).replace("{pvpbuilder_z}", String.valueOf(playerLoc.getBlockZ())));
                }
            }
        }
    }
}