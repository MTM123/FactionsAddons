package lv.mtm123.factionsaddons.modules;

import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.data.FACropHopper;
import lv.mtm123.factionsaddons.data.FACropHopperManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class CropHoppersModule extends AbstractModule {

    private final FACropHopperManager cropHopperManager;

    public CropHoppersModule(FactionsAddons plugin, FACropHopperManager cropHopperManager) {
        super(plugin);
        this.cropHopperManager = cropHopperManager;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        cropHopperManager.loadHopperAtChunk(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){
        if(!cropHopperManager.hasCropHopper(event.getChunk()))
            return;

        cropHopperManager.saveHopper((FACropHopper) cropHopperManager.getCropHopperAtChunk(event.getChunk()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockBreakEvent event){

    }
}
