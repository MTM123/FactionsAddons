package lv.mtm123.factionsaddons.modules;

import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.data.FACropHopper;
import lv.mtm123.factionsaddons.data.FACropHopperManager;
import lv.mtm123.factionsaddons.data.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CropHoppersModule extends AbstractModule {

    private static final String LORE_ID = ChatColor.translateAlternateColorCodes('&', "&a&c&6");

    private final Messages msgs;
    private final FACropHopperManager cropHopperManager;

    public CropHoppersModule(FactionsAddons plugin, Messages msgs, FACropHopperManager cropHopperManager) {
        super(plugin);
        this.cropHopperManager = cropHopperManager;
        this.msgs = msgs;
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

        ItemStack i = event.getItemInHand();

        if(i.getType() != Material.HOPPER)
            return;

        if(!i.hasItemMeta() || !i.getItemMeta().hasLore())
            return;

        List<String> lore = i.getItemMeta().getLore();

        if(!lore.contains(LORE_ID))
            return;

        if(cropHopperManager.hasCropHopper(event.getBlock().getChunk())){
            event.setCancelled(true);
            event.getPlayer().sendMessage(msgs.get("crop-hopper-limit"));
            return;
        }


        cropHopperManager.handleCropHopperPlacement(event.getBlock().getLocation());

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event){

        if(event.getBlock().getType() != Material.HOPPER)
            return;

        if(!cropHopperManager.hasCropHopper(event.getBlock().getChunk()))
            return;

        if(!cropHopperManager.isCropHopper(event.getBlock().getLocation()))
            return;

        cropHopperManager.handleCropHopperRemoval(event.getBlock().getChunk());

    }

    public static String getLoreId() {
        return LORE_ID;
    }
}
