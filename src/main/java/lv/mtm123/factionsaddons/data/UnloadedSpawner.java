package lv.mtm123.factionsaddons.data;

import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class UnloadedSpawner implements Spawner {

    private final Location loc;
    private final int count;

    public UnloadedSpawner(Location loc, int count) {
        this.loc = loc;
        this.count = count;
    }

    @Override
    public int getSpawnerCount() {
        return count;
    }

    @Override
    public Location getLocation() {
        return loc;
    }

    @Override
    public EntityType getSpawnedEntityType() {
        CreatureSpawner sp = (CreatureSpawner) loc.getBlock().getState();
        return sp.getSpawnedType();
    }
}
