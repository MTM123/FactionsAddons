package lv.mtm123.factionsaddons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (!event.getRightClicked().isVisible()) {
            event.setCancelled(true);
        }
    }

}
