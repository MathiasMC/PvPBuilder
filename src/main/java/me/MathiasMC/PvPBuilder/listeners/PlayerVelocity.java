package me.MathiasMC.PvPBuilder.listeners;

import me.MathiasMC.PvPBuilder.PvPBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;

public class PlayerVelocity implements Listener {

    private final PvPBuilder plugin;

    public PlayerVelocity(final PvPBuilder plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent e) {
        if (plugin.action_no_knockback.contains(e.getPlayer().getUniqueId().toString())) {
            e.setCancelled(true);
        }
    }
}