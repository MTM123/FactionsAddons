package lv.mtm123.factionsaddons.data;

import lv.mtm123.factionsaddons.util.StringUtil;
import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.api.Spawner;
import lv.mtm123.factionsaddons.api.SpawnerManager;
import lv.mtm123.spigotutils.ConfigManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.*;

public class FASpawnerManager implements SpawnerManager{

    private final String spawnerStackFormat;
    private final int maxStackSize;
    private final double spawnCountModifier;

    private final FileConfiguration spawnerCfg;
    private final Map<Chunk, Set<Spawner>> spawnerChunks;

    public FASpawnerManager(FactionsAddons plugin, Messages msgs, FileConfiguration cfg, FileConfiguration spawnerCfg){
        this.spawnerStackFormat = msgs.getFormat("spawner-stack-format");
        this.maxStackSize = cfg.getInt("modules.spawners.max-spawner-stack-size");

        this.spawnCountModifier = cfg.getDouble("modules.spawners.spawn-count-modifier");

        this.spawnerCfg = spawnerCfg;
        this.spawnerChunks = new HashMap<>();
    }

    public boolean containsChunk(Chunk chunk){
        return spawnerChunks.containsKey(chunk);
    }

    @Override
    public Set<Spawner> getSpawnersInChunk(Chunk chunk){
        return Collections.unmodifiableSet(containsChunk(chunk) ? spawnerChunks.get(chunk) : new HashSet<>());
    }

    @Override
    public Set<Spawner> getLoadedSpawnersInChunk(Chunk chunk) {
        return null;
    }

    @Override
    public Set<Spawner> getAllSpawnersInChunk(Chunk chunk) {
        return null;
    }

    public boolean handleSpawnerAddition(Location loc){

        FASpawner sp = null;
        if(containsChunk(loc.getChunk())){
            for(Spawner s : spawnerChunks.get(loc.getChunk())){
                if(s.getLocation().equals(loc)){
                    sp = (FASpawner) s;
                    break;
                }
            }
        }

        if(sp == null){
            sp = new FASpawner(loc);
            sp.setSpawnerCount(2, spawnCountModifier);
            sp.setHologramName(spawnerStackFormat);
            return true;
        }else{
            if(sp.getSpawnerCount() < maxStackSize){

                sp.setSpawnerCount(sp.getSpawnerCount() + 1, spawnCountModifier);
                sp.setHologramName(spawnerStackFormat);

                return true;
            }else{
                return false;
            }
        }

    }

    public int handleSpawnerSubtraction(FASpawner sp, int spawnerDecrement){

        int spawnerCount = sp.getSpawnerCount();

        int spawnersLeft = spawnerCount - spawnerDecrement;
        if(spawnersLeft <= 0){
            handleBlockBreaking(sp.getLocation());
        }else{
            sp.setSpawnerCount(spawnersLeft, spawnCountModifier);
            sp.setHologramName(spawnerStackFormat);
        }

        return spawnersLeft >=0 ? spawnerDecrement : spawnerCount;
    }

    public void handleSpawnerPlacement(Location loc, EntityType type){
        FASpawner sp = new FASpawner(loc);
        sp.setHologramName(spawnerStackFormat, type);

        saveSpawner(sp);

        if(containsChunk(loc.getChunk())){
            Set<Spawner> spawners = spawnerChunks.get(loc.getChunk());
            spawners.add(sp);
        }else{
            Set<Spawner> spawners = new HashSet<>();
            spawners.add(sp);
            spawnerChunks.put(loc.getChunk(), spawners);
        }
    }

    public void handleBlockBreaking(Location loc){

        Set<Spawner> spawners = spawnerChunks.get(loc.getChunk());
        FASpawner sp = null;
        for(Spawner s : spawners){
            if(s.getLocation().equals(loc)){
                sp = (FASpawner) s;
                break;
            }
        }

        if(sp != null){
            sp.removeHologram();
            deleteSpawner(sp);
            spawners.remove(sp);
        }

    }


    public void loadSpawnersInChunk(Chunk chunk){

        World w = chunk.getWorld();
        String path = w.getName() + "." + StringUtil.locToChunkKey(chunk);
        if(!spawnerCfg.contains(path)) return;

        Set<Spawner> spawners = new HashSet<>();
        for(String key : spawnerCfg.getConfigurationSection(path).getKeys(false)) {

            String[] stringLoc = key.split("_");
            if(stringLoc.length != 3) continue;

            int x = Integer.parseInt(stringLoc[0]);
            int y = Integer.parseInt(stringLoc[1]);
            int z = Integer.parseInt(stringLoc[2]);

            int spawnerCount = spawnerCfg.getInt(path + "." + key + ".spawner-count");

            Location loc = new Location(w, x, y, z);
            if(loc.getBlock().getType() == Material.MOB_SPAWNER){
                FASpawner sp = new FASpawner(loc, spawnerCount);

                sp.setHologramName(spawnerStackFormat);

                spawners.add(sp);
            } else {
                spawnerCfg.set(path + "." + key, null);
                ConfigManager.save(spawnerCfg, "spawners.yml");
            }



        }

        if(spawners.size() > 0){
            spawnerChunks.put(chunk, spawners);
        }

    }

    public void saveSpawnersAndUnload(Chunk chunk){
        if(containsChunk(chunk)){
            for(Spawner s : spawnerChunks.get(chunk)){
                ((FASpawner) s).removeHologram();

                String key = chunk.getWorld().getName()
                        + "." + StringUtil.locToChunkKey(chunk)
                        + "." + StringUtil.locToLocKey(s.getLocation());

                spawnerCfg.set(key + ".spawner-count", s.getSpawnerCount());
            }

            spawnerChunks.remove(chunk);
            ConfigManager.save(spawnerCfg, "spawners.yml");
        }
    }

    private void saveSpawner(FASpawner spawner){

        Location loc = spawner.getLocation();
        String key = loc.getWorld().getName()
                + "." + StringUtil.locToChunkKey(loc.getChunk())
                + "." + StringUtil.locToLocKey(loc);

        spawnerCfg.set(key + ".spawner-count", spawner.getSpawnerCount());

        ConfigManager.save(spawnerCfg, "spawners.yml");

    }

    private void deleteSpawner(FASpawner spawner){

        Location loc = spawner.getLocation();
        String key = loc.getWorld().getName()
                + "." + StringUtil.locToChunkKey(loc.getChunk())
                + "." + StringUtil.locToLocKey(loc);

        spawnerCfg.set(key , null);

        ConfigManager.save(spawnerCfg, "spawners.yml");
    }

    public void saveAllAndUnload(){
        for(Map.Entry<Chunk, Set<Spawner>> e : spawnerChunks.entrySet()){

            String chunkKey = e.getKey().getWorld().getName() + "." + StringUtil.locToChunkKey(e.getKey());
            for(Spawner s : e.getValue()){
                ((FASpawner) s).removeHologram();

                String key = chunkKey + "." + StringUtil.locToLocKey(s.getLocation());
                spawnerCfg.set(key + ".spawner-count", s.getSpawnerCount());
            }

        }

        ConfigManager.save(spawnerCfg, "spawners.yml");

        spawnerChunks.clear();
    }

}
