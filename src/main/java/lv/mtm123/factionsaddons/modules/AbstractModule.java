package lv.mtm123.factionsaddons.modules;

import lv.mtm123.factionsaddons.FactionsAddons;
import org.bukkit.event.Listener;

abstract class AbstractModule implements Listener {

    AbstractModule(FactionsAddons plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}
