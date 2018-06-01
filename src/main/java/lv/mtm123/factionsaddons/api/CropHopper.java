package lv.mtm123.factionsaddons.api;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public interface CropHopper {

    /**
     * Returns location of the hopper
     * @return Location of the hopper
     */
    Location getLocation();

    /**
     * Returns inventory of the hopper
     * @return Inventory of the hopper
     */
    Inventory getInventory();

}
