package me.mtm123.factionsaddons.listeners.player;

import me.mtm123.factionsaddons.PlayerSettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLoginStateListener implements Listener {

    private final PlayerSettingsManager pSettingsManager;

    public PlayerLoginStateListener(PlayerSettingsManager pSettingsManager){
        this.pSettingsManager = pSettingsManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAsyncPreLoginEvent(AsyncPlayerPreLoginEvent event){
        pSettingsManager.loadPlayerSettings(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event){
        pSettingsManager.saveAndRemove(event.getPlayer());
    }

}
