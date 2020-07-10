package me.MathiasMC.PvPBuilder.listeners;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {

    private final PvPBuilder plugin;

    public EntityDamageByEntity(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntity(EntityDamageByEntityEvent e) {
        plugin.ironGolemManager.onEntity(e);
    }
}