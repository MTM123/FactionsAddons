package lv.mtm123.factionsaddons.api;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * Represents Stackable spawner
 */
public interface Spawner {

    /**
     * Returns how many spawners are stacked in block
     *
     * @return int Spawner count
     */
    int getSpawnerCount();

    /**
     * Returns location of spawner
     *
     * @return Spawner location
     */
    Location getLocation();

    /**
     * Returns spawned entity type
     *
     * @return Spawned EntityType
     */
    EntityType getSpawnedEntityType();

}
