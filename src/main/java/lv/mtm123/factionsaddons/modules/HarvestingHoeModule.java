package lv.mtm123.factionsaddons.modules;

import com.google.common.collect.Iterables;
import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.data.Messages;
import lv.mtm123.spigotutils.InvUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HarvestingHoeModule extends AbstractModule {

    private final static String LORE_ID = ChatColor.translateAlternateColorCodes('&', "&a&c&5");

    private final Messages msgs;

    private final Set<Material> harvestableBlocks;
    private final boolean harvestOnFullInv;


    public HarvestingHoeModule(FactionsAddons plugin, Messages msgs, FileConfiguration cfg) {
        super(plugin);
        this.msgs = msgs;
        this.harvestableBlocks = new HashSet<>();
        this.harvestOnFullInv = cfg.getBoolean("modules.harvesting-hoe.harvest-on-full-inv");
        loadHarvestableBlocks(cfg);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){

        if(!harvestableBlocks.contains(event.getBlock().getType()))
            return;

        if(event.getPlayer().getItemInHand() == null)
            return;

        ItemStack i = event.getPlayer().getItemInHand();
        if(!i.hasItemMeta() || !i.getItemMeta().hasLore())
            return;

        if(i.getItemMeta().getLore().contains(LORE_ID)){

            event.setCancelled(true);

            Block b = event.getBlock();

            Collection<ItemStack> drops = b.getDrops();
            if(drops.size() > 0){

                Player player = event.getPlayer();

                int spaceRequired = drops.stream().mapToInt(ItemStack::getAmount).sum();
                int freeSpace = InvUtil.getFreeSpaceForItem(player, Iterables.get(drops, 0));

                if(spaceRequired <= freeSpace){
                    player.getInventory().addItem(drops.toArray(new ItemStack[]{}));
                    if(b.getType() == Material.SUGAR_CANE_BLOCK || b.getType() == Material.CACTUS){
                        callEventsForMultiBlockHarvestables(b, event.getPlayer());
                    }
                    b.setType(Material.AIR);
                }else{
                    if(!harvestOnFullInv){
                        for(ItemStack d : player.getInventory().addItem(drops.toArray(new ItemStack[]{})).values()){
                            event.getBlock().getWorld().dropItemNaturally(b.getLocation(), d);
                        }
                        if(b.getType() == Material.SUGAR_CANE_BLOCK || b.getType() == Material.CACTUS){
                            callEventsForMultiBlockHarvestables(b, event.getPlayer());
                        }
                        b.setType(Material.AIR);
                    }else{
                        player.sendMessage(msgs.get("harvest-hoe-inv-full"));
                    }
                }

            }


        }

    }



    private void loadHarvestableBlocks(FileConfiguration cfg){
        for(String li : cfg.getStringList("modules.harvesting-hoe.harvestable-blocks")){
            Material mat = Material.matchMaterial(li);
            if(mat != null){
                harvestableBlocks.add(mat);
            }
        }
    }

    private void callEventsForMultiBlockHarvestables(Block startBlock, Player player){

        Material mat = startBlock.getType();
        while (mat != Material.AIR){
            startBlock = startBlock.getRelative(0, 1, 0);
            mat = startBlock.getType();
            if(startBlock.getType() == mat){
                Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(startBlock, player));
            }
        }

    }

    public static String getLoreId() {
        return LORE_ID;
    }
}
