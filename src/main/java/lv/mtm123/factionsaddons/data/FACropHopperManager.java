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
        return hoppers.get(chunk);
    }

    @Override
    public boolean isCropHopper(Location loc) {
        return hasCropHopper(loc.getChunk()) && hoppers.get(loc.getChunk()).getLocation().equals(loc);
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

    public void handleCropHopperPlacement(Location loc){
        FACropHopper cropHopper = new FACropHopper(loc);

        saveHopper(cropHopper);

        hoppers.put(loc.getChunk(), cropHopper);
    }

    public void handleCropHopperRemoval(Chunk chunk){
        FACropHopper cropHopper = hoppers.get(chunk);

        deleteHopper(cropHopper);
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

    private void deleteHopper(FACropHopper hopper) {
        String key = hopper.getLocation().getWorld().getName() + "." + StringUtil.locToChunkKey(hopper.getLocation().getChunk());

        cropHopperCfg.set(key, null);

        ConfigManager.save(cropHopperCfg, "hoppers.yml");
    }

    public void saveAllAndUnload(){
        hoppers.forEach((c, h) -> {
            String key = h.getLocation().getWorld().getName() + "." + StringUtil.locToChunkKey(c);
            String val = StringUtil.locToLocKey(h.getLocation());

            cropHopperCfg.set(key, val);
        });

        ConfigManager.save(cropHopperCfg, "hoppers.yml");
    }

}
