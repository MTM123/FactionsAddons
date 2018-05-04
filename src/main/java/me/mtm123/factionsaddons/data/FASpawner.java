package me.mtm123.factionsaddons.data;

import me.mtm123.factionsaddons.api.Spawner;
import me.mtm123.factionsaddons.util.StringUtil;
import me.mtm123.spigotutils.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FASpawner implements Spawner{

    private final Location loc;

    private ArmorStand as;
    private int spawnerCount;

    public FASpawner(Location loc){
        this.loc = loc;
        this.spawnerCount = 1;
        spawnHologram();
    }

    public FASpawner(Location loc, int spawnerCount){
        this.loc = loc;
        this.spawnerCount = spawnerCount;
        spawnHologram();
    }

    public void setSpawnerCount(int spawnerCount, double modifier){

        try {
            Class<?> cCraftCreatureSpawner = ReflectionUtil.getClass(ReflectionUtil.Package.CRAFTBUKKIT, "block.CraftCreatureSpawner" );
            Class<?> cTileEntityMobSpawner = ReflectionUtil.getClass(ReflectionUtil.Package.MINECRAFT, "TileEntityMobSpawner");
            Class<?> cMobSpawnerAbstract = ReflectionUtil.getClass(ReflectionUtil.Package.MINECRAFT, "MobSpawnerAbstract");

            Object oCraftCreatureSpawner = cCraftCreatureSpawner.cast(loc.getBlock().getState());
            Method getTileEntity = cCraftCreatureSpawner.getDeclaredMethod("getTileEntity");

            Object spawnerTileEntity =  getTileEntity.invoke(oCraftCreatureSpawner);
            Method getSpawner = cTileEntityMobSpawner.getDeclaredMethod("getSpawner");

            Object oMobSpawnerAbstract = getSpawner.invoke(spawnerTileEntity);

            Field f = cMobSpawnerAbstract.getDeclaredField("spawnCount");

            f.setAccessible(true);
            f.set(oMobSpawnerAbstract, (int) Math.round(spawnerCount * 4 * modifier));
            f.setAccessible(false);


            Field d = cMobSpawnerAbstract.getDeclaredField("maxNearbyEntities");

            d.setAccessible(true);
            d.set(oMobSpawnerAbstract, (int) Math.round(spawnerCount * 6 * modifier));
            d.setAccessible(false);

            this.spawnerCount = spawnerCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSpawnerCount() {
        return spawnerCount;
    }

    @Override
    public Location getLocation() {
        return loc;
    }

    @Override
    public EntityType getSpawnedEntityType(){
        CreatureSpawner sp = (CreatureSpawner) loc.getBlock().getState();
        return sp.getSpawnedType();
    }


    void removeHologram(){
        as.remove();
    }

    void setHologramName(String format){
        EntityType type = ((CreatureSpawner) loc.getBlock().getState()).getSpawnedType();
        as.setCustomName(format.replace("%amount%", String.valueOf(spawnerCount))
                .replace("%mobtype%",
                        StringUtil.getNormalizedSpawnerName(type)));
    }

    void setHologramName(String format, EntityType type){
        as.setCustomName(format.replace("%amount%", String.valueOf(spawnerCount))
                .replace("%mobtype%", StringUtil.getNormalizedSpawnerName(type)));
    }

    private void spawnHologram(){
        as = loc.getWorld().spawn(loc.clone().add(0.5, 1, 0.5), ArmorStand.class);
        as.setGravity(false);
        as.setSmall(true);
        as.setMarker(true);
        as.setVisible(false);
        as.setCustomNameVisible(true);
    }


}
