package me.MathiasMC.PvPBuilder.managers;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

public class IronGolemManager {

    private final PvPBuilder plugin;

    public final HashMap<IronGolem, String> ironGolemGroup = new HashMap<>();
    public final HashMap<IronGolem, Player> ironGolemOwner = new HashMap<>();

    public IronGolemManager(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    public void run(long update, long delay, long radius, long tpRadius, Player target, IronGolem ironGolem) {
        int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (target.getLocation().distance(ironGolem.getLocation()) > tpRadius) {
                ironGolem.teleport(target);
            }
            LivingEntity livingEntity = getClose(ironGolem.getLocation(), getEntityAround(target, radius));
            if (livingEntity != null) {
                ironGolem.setTarget(livingEntity);
            }
        }, 0, update);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getServer().getScheduler().cancelTask(id);
            for (String command : plugin.config.get.getStringList("iron_golem." + ironGolemGroup.get(ironGolem) + ".commands.remove.disappear")) {
                plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{pvpbuilder_player}", ironGolemOwner.get(ironGolem).getName()));
            }
            ironGolemGroup.remove(ironGolem);
            ironGolemOwner.remove(ironGolem);
            ironGolem.remove();
        }, delay * 20L);
    }

    public ArrayList<LivingEntity> getEntityAround(Player player, double r) {
        ArrayList<LivingEntity> list = new ArrayList<>();
        for (Entity entity : player.getNearbyEntities(r, r, r)) {
            if (entity instanceof Player || entity instanceof org.bukkit.entity.Monster || entity instanceof org.bukkit.entity.Animals) {
                list.add((LivingEntity) entity);
            }
        }
        return list;
    }

    public LivingEntity getClose(Location location, ArrayList<LivingEntity> entity) {
        double max = Double.MAX_VALUE;
        LivingEntity livingEntity = null;
        for(LivingEntity key : entity) {
            double distance = key.getLocation().distance(location);
            if (max == Double.MAX_VALUE || distance < max) {
                max = distance;
                livingEntity = key;
            }
        }
        return livingEntity;
    }

    public void removeAll() {
        for (IronGolem ironGolem : ironGolemGroup.keySet()) {
            ironGolem.remove();
        }
    }

    public void onEntity(EntityDamageByEntityEvent e) {
        IronGolem ironGolem = getIronGolem(e);
        if (ironGolem != null && ironGolemGroup.containsKey(ironGolem) && ironGolemOwner.containsKey(ironGolem)) {
            Player player = ironGolemOwner.get(ironGolem);
            if (player != null && player.isOnline()) {
                if (e.getEntity() instanceof Player) {
                    for (String command : plugin.config.get.getStringList("iron_golem." + ironGolemGroup.get(ironGolem) + ".commands.player")) {
                        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{pvpbuilder_player}", player.getName()).replace("{pvpbuilder_target}", e.getEntity().getName()));
                    }
                } else if (e.getEntity() instanceof Monster) {
                    for (String command : plugin.config.get.getStringList("iron_golem." + ironGolemGroup.get(ironGolem) + ".commands.mob")) {
                        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{pvpbuilder_player}", player.getName()).replace("{pvpbuilder_target}", e.getEntity().getName()));
                    }
                } else if (e.getEntity() instanceof Animals) {
                    for (String command : plugin.config.get.getStringList("iron_golem." + ironGolemGroup.get(ironGolem) + ".commands.animal")) {
                        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{pvpbuilder_player}", player.getName()).replace("{pvpbuilder_target}", e.getEntity().getName()));
                    }
                }
            }
        }
    }

    private IronGolem getIronGolem(EntityDamageByEntityEvent e) {
        LivingEntity damaged = (LivingEntity)e.getEntity();
        if (damaged.getHealth() <= e.getDamage()) {
            Entity damager = e.getDamager();
            if (damager instanceof IronGolem) {
                return (IronGolem) damager;
            }
        }
        return null;
    }
}