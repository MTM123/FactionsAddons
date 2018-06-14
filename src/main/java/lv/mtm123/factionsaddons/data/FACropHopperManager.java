package lv.mtm123.factionsaddons.data;

import lv.mtm123.factionsaddons.api.CropHopper;
import lv.mtm123.factionsaddons.api.CropHopperManager;
import lv.mtm123.factionsaddons.util.StringUtil;
import lv.mtm123.spigotutils.ConfigManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class FACropHopperManager implements CropHopperManager {

    private final FileConfiguration cropHopperCfg;

    private final Map<Chunk, FACropHopper> hoppers;

    public FACropHopperManager(FileConfiguration cropHopperCfg) {
        this.cropHopperCfg = cropHopperCfg;
        this.hoppers = new HashMap<>();
    }

    @Override
    public CropHopper getCropHopperAtChunk(Chunk chunk) {
        return null;
    }

    @Override
    public boolean isCropHopper(Location loc) {
        return false;
    }

    @Override
    public boolean hasCropHopper(Chunk chunk) {
        if (hoppers.containsKey(chunk)) {
            return true;
        } else {
            String key = StringUtil.locToChunkKey(chunk);
            return cropHopperCfg.contains(key);
        }
    }

    public void loadHopperAtChunk(Chunk chunk) {

        if (!hasCropHopper(chunk))
            return;

        String key = chunk.getWorld().getName() + "." + StringUtil.locToChunkKey(chunk);
        String val = cropHopperCfg.getString(key);

        Location loc = StringUtil.keyToLoc(chunk.getWorld(), val);

        FACropHopper hopper = new FACropHopper(loc);

        hoppers.put(chunk, hopper);

    }

    public void saveHopper(FACropHopper hopper) {
        String key = hopper.getLocation().getWorld().getName() + "." + StringUtil.locToChunkKey(hopper.getLocation().getChunk());
        String val = StringUtil.locToLocKey(hopper.getLocation());

        cropHopperCfg.set(key, val);

        ConfigManager.save(cropHopperCfg, "hoppers.yml");
    }

    public void deleteHopper(FACropHopper hopper) {
        String key = hopper.getLocation().getWorld().getName() + "." + StringUtil.locToChunkKey(hopper.getLocation().getChunk());

        cropHopperCfg.set(key, null);

        ConfigManager.save(cropHopperCfg, "hoppers.yml");
    }

}
