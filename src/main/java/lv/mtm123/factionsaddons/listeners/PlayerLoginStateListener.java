package lv.mtm123.factionsaddons.listeners;

import lv.mtm123.factionsaddons.data.FAPlayerSettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLoginStateListener implements Listener {

    private final FAPlayerSettingsManager pSettingsManager;

    public PlayerLoginStateListener(FAPlayerSettingsManager pSettingsManager){
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
