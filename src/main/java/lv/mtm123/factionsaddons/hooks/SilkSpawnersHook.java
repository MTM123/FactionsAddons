package lv.mtm123.factionsaddons.hooks;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import de.dustplanet.util.SilkUtil;
import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.api.Spawner;
import lv.mtm123.factionsaddons.data.FASpawner;
import lv.mtm123.factionsaddons.data.FASpawnerManager;
import lv.mtm123.factionsaddons.data.Messages;
import lv.mtm123.factionsaddons.modules.SpawnerModule;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class SilkSpawnersHook implements Listener {

    private final FactionsAddons plugin;
    private final FASpawnerManager spawnerManager;
    private final Messages msgs;
    private final SilkUtil sUtil;

    private final boolean ssHandleExplosions;
    private final double explosionDropModifier;
    private final boolean cancelSpawnerPlacementNoPerm;
    private final int spawnerDecrement;


    public SilkSpawnersHook(FactionsAddons plugin, FASpawnerManager spawnerManager, Messages msgs, SilkUtil sUtil, FileConfiguration cfg) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.spawnerManager = spawnerManager;
        this.msgs = msgs;
        this.sUtil = sUtil;

        this.ssHandleExplosions = cfg.getBoolean("hooks.silk-spawners-handle-drops-on-explosion");
        this.cancelSpawnerPlacementNoPerm = cfg.getBoolean("modules.spawners.cancel-spawner-placement-without-perm");
        this.spawnerDecrement = cfg.getInt("modules.spawners.spawner-break-decrement");

        double exp = cfg.getDouble("modules.spawners.explosion-drop-modifier");
        this.explosionDropModifier = exp >= 0 ? exp : 0;

    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!ssHandleExplosions)
            return;

        for (Block b : event.blockList()) {
            if (b.getType() == Material.MOB_SPAWNER) {
                b.setMetadata("exploded", new FixedMetadataValue(plugin, ""));
                SilkSpawnersSpawnerBreakEvent breakEvent = new SilkSpawnersSpawnerBreakEvent(null, b, sUtil.getSpawnerEntityID(b));
                Bukkit.getServer().getPluginManager().callEvent(breakEvent);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSilkSpawnerBreak(SilkSpawnersSpawnerBreakEvent event) {

        Block b = event.getBlock();

        int dropAmount = 1;
        if (spawnerManager.containsChunk(b.getChunk())) {
            for (Spawner s : spawnerManager.getSpawnersInChunk(b.getChunk())) {
                if (s.getLocation().equals(b.getLocation())) {
                    if (spawnerDecrement > 0) {
                        int spawnerAmount = s.getSpawnerCount();
                        dropAmount = spawnerManager.handleSpawnerSubtraction((FASpawner) s, spawnerDecrement);
                        if (spawnerAmount <= dropAmount) {
                            event.getBlock().setType(Material.AIR);
                        }
                    } else {
                        dropAmount = s.getSpawnerCount();
                        spawnerManager.handleBlockBreaking(event.getBlock().getLocation());
                        event.getBlock().setType(Material.AIR);
                    }
                    break;
                }
            }
        }

        if (event.getPlayer() != null
                && event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (b.hasMetadata("exploded")) {
            b.removeMetadata("exploded", plugin);
            dropAmount = (int) Math.floor(dropAmount * explosionDropModifier);
        }

        if (dropAmount > 0) {
            int stacks = (int) Math.floor(dropAmount / 64);
            int leftOver = dropAmount - stacks * 64;

            ItemStack i = sUtil.newSpawnerItem(event.getEntityID(), sUtil.getCustomSpawnerName(sUtil.eid2MobID.get(event.getEntityID())));
            while (stacks > 0) {
                i = i.clone();
                i.setAmount(64);
                b.getWorld().dropItemNaturally(b.getLocation(), i);
                stacks--;
            }

            if (leftOver > 0) {
                i.setAmount(leftOver);
                b.getWorld().dropItemNaturally(b.getLocation(), i);
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSpawnerPlace(SilkSpawnersSpawnerPlaceEvent event) {

        EntityType type = EntityType.fromName(sUtil.eid2MobID.get(event.getEntityID()));
        if (type == null || SpawnerModule.getSpawnerStackBlacklist().contains(type))
            return;

        spawnerManager.handleSpawnerPlacement(event.getBlock().getLocation(), type);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getPlayer().isSneaking())
            return;

        if (event.getClickedBlock().getType() != Material.MOB_SPAWNER)
            return;

        if (event.getItem() == null
                || event.getItem().getType() != Material.MOB_SPAWNER
                || !event.getItem().hasItemMeta())
            return;

        ItemStack i = event.getItem();
        EntityType itype = EntityType.fromId(sUtil.getStoredSpawnerItemEntityID(i));
        EntityType btype = EntityType.fromId(sUtil.getSpawnerEntityID(event.getClickedBlock()));

        if (itype != btype || itype == null)
            return;

        if (SpawnerModule.getSpawnerStackBlacklist().contains(btype))
            return;

        Player player = event.getPlayer();
        if (player.hasPermission("fa.spawners.stack")) {

            if (spawnerManager.handleSpawnerAddition(event.getClickedBlock().getLocation())) {

                int amount = i.getAmount() - 1;
                if (amount > 0) {
                    i.setAmount(amount);
                } else {
                    player.setItemInHand(null);
                }

            } else {
                player.sendMessage(msgs.get("spawner-max-stack-size-reached"));
            }

            event.setCancelled(true);
        } else {
            if (cancelSpawnerPlacementNoPerm) {
                event.setCancelled(true);
                player.sendMessage(msgs.get("no-permission.stacking"));
            }
        }
    }

}
