package lv.mtm123.factionsaddons.data;

import lv.mtm123.factionsaddons.api.CropHopper;
import lv.mtm123.factionsaddons.api.CropHopperManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class FACropHopperManager implements CropHopperManager {

    private final FileConfiguration cropHopperCfg;

    public FACropHopperManager() {
        cropHopperCfg = null;
    }


    @Override
    public CropHopper getCropHopperAtChunk(Chunk chunk) {
        return null;
    }

    @Override
    public boolean isCropHopper(Location loc) {
        return false;
    }

    public void loadHopperAtChunk(){

    }


}
