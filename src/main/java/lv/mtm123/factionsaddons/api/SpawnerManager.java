package lv.mtm123.factionsaddons.api;

import org.bukkit.Chunk;

import java.util.Set;

/**
 * Represents spawner manager
 */
public interface SpawnerManager {

    /**
     * Returns all spawners in certain chunk
     *
     * @param chunk Chunk to retrieve spawners for
     * @return Unmodifiable Set of Spawners at that Chunk
     *
     * @deprecated subject to change
     */
    @Deprecated
    Set<Spawner> getSpawnersInChunk(Chunk chunk);

    /**
     * Checks if there are spawners for that chunk
     *
     * @param chunk Chunk to check
     * @return True if there are spawners for that chunk
     */
    boolean containsChunk(Chunk chunk);

}
