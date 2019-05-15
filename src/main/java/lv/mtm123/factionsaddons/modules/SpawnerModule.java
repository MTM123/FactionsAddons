package lv.mtm123.factionsaddons.modules;

import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.api.Spawner;
import lv.mtm123.factionsaddons.data.FASpawner;
import lv.mtm123.factionsaddons.data.FASpawnerManager;
import lv.mtm123.factionsaddons.data.Messages;
import lv.mtm123.spigotutils.item.NBTUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SpawnerModule extends AbstractModule {

    private final FASpawnerManager spawnerManager;
    private final Messages msgs;

    private final boolean silkSpawnersHookEnabled;

    private final double explosionDropModifier;
    private final boolean removeWithSilktouch;
    private final boolean cancelSpawnerPlacementNoPerm;
    private final boolean blockSpawnerMoveToAnvil;
    private final int spawnerDecrement;

    private static final Set<EntityType> spawnerStackBlacklist = new HashSet<>();

    public SpawnerModule(FactionsAddons plugin, FASpawnerManager spawnerManager, Messages msgs, FileConfiguration cfg, boolean silkSpawnersHookEnabled) {
        super(plugin);

        this.spawnerManager = spawnerManager;
        this.msgs = msgs;

        this.silkSpawnersHookEnabled = silkSpawnersHookEnabled;

        double exp = cfg.getDouble("modules.spawners.explosion-drop-modifier");
        this.explosionDropModifier = exp >= 0 ? exp : 0;

        this.removeWithSilktouch = cfg.getBoolean("modules.spawners.remove-with-silktouch");
        this.cancelSpawnerPlacementNoPerm = cfg.getBoolean("modules.spawners.cancel-spawner-placement-without-perm");
        this.blockSpawnerMoveToAnvil = cfg.getBoolean("modules.spawners.block-placement-in-anvils");

        this.spawnerDecrement = cfg.getInt("modules.spawners.spawner-break-decrement");

        loadSpawnerBlacklist(cfg);

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() != Material.MOB_SPAWNER)
            return;

        if (silkSpawnersHookEnabled)
            return;

        ItemStack i = event.getItemInHand();
        BlockStateMeta imeta = (BlockStateMeta) i.getItemMeta();
        EntityType itype = ((CreatureSpawner) imeta.getBlockState()).getSpawnedType();

        if (spawnerStackBlacklist.contains(itype))
            return;

        spawnerManager.handleSpawnerPlacement(event.getBlockPlaced().getLocation(), itype);

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.MOB_SPAWNER)
            return;

        if (silkSpawnersHookEnabled)
            return;

        Block b = event.getBlock();

        int dropAmount = 1;
        if (spawnerManager.containsChunk(b.getChunk())) {
            for (Spawner s : spawnerManager.getSpawnersInChunk(b.getChunk())) {
                if (s.getLocation().equals(b.getLocation())) {
                    if (spawnerDecrement > 0) {
                        int spawnerAmount = s.getSpawnerCount();
                        dropAmount = spawnerManager.handleSpawnerSubtraction((FASpawner) s, spawnerDecrement);
                        if (spawnerAmount > dropAmount) {
                            event.setCancelled(true);
                        }
                    } else {
                        dropAmount = s.getSpawnerCount();
                        spawnerManager.handleBlockBreaking(event.getBlock().getLocation());
                    }
                    break;
                }
            }
        }

        if (removeWithSilktouch) {
            Player player = event.getPlayer();
            if (player.getItemInHand() != null) {
                ItemStack i = player.getItemInHand();
                if (i.getEnchantments().containsKey(Enchantment.SILK_TOUCH)
                        && player.hasPermission("fa.spawners.silktouch")) {

                    EntityType type = ((CreatureSpawner) b.getState()).getSpawnedType();

                    createSpawnerItemStacks(type, dropAmount)
                            .forEach(item -> b.getWorld().dropItemNaturally(b.getLocation(), item));
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        if (silkSpawnersHookEnabled)
            return;

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
        BlockStateMeta imeta = (BlockStateMeta) i.getItemMeta();
        EntityType itype = ((CreatureSpawner) imeta.getBlockState()).getSpawnedType();
        EntityType btype = ((CreatureSpawner) event.getClickedBlock().getState()).getSpawnedType();

        if (itype != btype)
            return;

        if (spawnerStackBlacklist.contains(btype))
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(EntityExplodeEvent event) {

        for (Block b : event.blockList()) {

            if (b.getType() == Material.MOB_SPAWNER) {

                Set<Spawner> spawners = spawnerManager.getSpawnersInChunk(b.getChunk());

                Spawner sp = null;
                for (Spawner s : spawners) {
                    if (s.getLocation().equals(b.getLocation())) {
                        sp = s;
                        break;
                    }
                }


                if (sp != null) {

                    spawnerManager.handleBlockBreaking(sp.getLocation());


                    int spawnerDropAmount = (int) Math.floor(sp.getSpawnerCount() * explosionDropModifier);
                    if (spawnerDropAmount > 0) {
                        createSpawnerItemStacks(sp.getSpawnedEntityType(), spawnerDropAmount)
                                .forEach(e -> b.getWorld().dropItemNaturally(b.getLocation(), e));
                    }

                } else {

                    EntityType type = ((CreatureSpawner) b.getState()).getSpawnedType();

                    int spawnerDropAmount = (int) Math.floor(1 * explosionDropModifier);
                    if (spawnerDropAmount > 0) {
                        createSpawnerItemStacks(type, spawnerDropAmount)
                                .forEach(e -> b.getWorld().dropItemNaturally(b.getLocation(), e));
                    }

                }

            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        spawnerManager.loadSpawnersInChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        spawnerManager.saveSpawnersAndUnload(event.getChunk());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemMove(InventoryClickEvent event) {
        if (!blockSpawnerMoveToAnvil)
            return;

        if (event.getView().getTopInventory().getType() != InventoryType.ANVIL)
            return;

        if (event.getCurrentItem().getType() == Material.MOB_SPAWNER
                || event.getCursor().getType() == Material.MOB_SPAWNER
                || event.getClick() == ClickType.NUMBER_KEY
                && event.getView().getBottomInventory()
                .getItem(event.getHotbarButton()).getType() == Material.MOB_SPAWNER) {

            event.setCancelled(true);
        }

    }

    private static List<ItemStack> createSpawnerItemStacks(EntityType type, int amount) {

        List<ItemStack> itemStacks = new ArrayList<>();

        int stacks = (int) Math.floor(amount / 64);
        int leftOver = amount - stacks * 64;

        String entityId = type.getName();

        String displayName = entityId;
        if (type == EntityType.IRON_GOLEM) {
            displayName = displayName.replace("Villager", "Iron");
        } else if (type == EntityType.PIG_ZOMBIE) {
            displayName = displayName.replace("PigZombie", "ZombiePigman");
        }

        displayName = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(displayName), " ") + " Spawner";

        ItemStack i = new ItemStack(Material.MOB_SPAWNER, 1);
        NBTUtil.NBTTag entityData = new NBTUtil.NBTTag();
        entityData.set("EntityId", entityId);
        entityData.set("MaxNearbyEntities", (short) 6);
        entityData.set("RequiredPlayerRange", (short) 16);
        entityData.set("SpawnCount", (short) 4);
        entityData.set("x", 0);
        entityData.set("y", 0);
        entityData.set("z", 0);
        entityData.set("id", "MobSpawner");
        entityData.set("MaxSpawnDelay", (short) 800);
        entityData.set("SpawnRange", (short) 4);
        entityData.set("Delay", (short) 20);
        entityData.set("MinSpawnDelay", (short) 200);

        Map<String, Object> data = new HashMap<>();
        data.put("BlockEntityTag", entityData);

        NBTUtil.NBTTag display = new NBTUtil.NBTTag();
        display.set("Name", displayName + "aaaa");
        data.put("display", display);

        try {
            i = NBTUtil.addNBTDataToItem(i, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ItemMeta imeta = i.getItemMeta();
        imeta.setDisplayName(ChatColor.RESET + displayName);
        i.setItemMeta(imeta);

        while (stacks > 0) {
            ItemStack ni = i.clone();
            ni.setAmount(64);
            itemStacks.add(ni);
            stacks--;
        }

        if (leftOver > 0) {
            i.setAmount(leftOver);
            itemStacks.add(i);
        }

        return itemStacks;
    }

    private void loadSpawnerBlacklist(FileConfiguration cfg) {
        for (String e : cfg.getStringList("modules.spawners.spawner-stack-blacklist")) {
            EntityType type = EntityType.fromName(e.toUpperCase().replace("-", "_"));
            if (type != null)
                spawnerStackBlacklist.add(type);
        }
    }

    public static Set<EntityType> getSpawnerStackBlacklist() {
        return spawnerStackBlacklist;
    }

}
