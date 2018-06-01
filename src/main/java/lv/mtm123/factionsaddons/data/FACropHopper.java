package lv.mtm123.factionsaddons.data;

import lv.mtm123.factionsaddons.api.CropHopper;
import org.bukkit.Location;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.Inventory;

public class FACropHopper implements CropHopper {

    private final Location loc;

    public FACropHopper(Location loc) {
        this.loc = loc;
    }

    @Override
    public Location getLocation() {
        return loc;
    }

    @Override
    public Inventory getInventory() {
        Hopper hopper = (Hopper) loc.getBlock().getState();
        return hopper.getInventory();
    }
}
