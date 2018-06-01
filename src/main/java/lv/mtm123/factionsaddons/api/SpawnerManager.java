package lv.mtm123.factionsaddons.api;

import org.bukkit.Chunk;

import java.util.Set;

/**
 * Represents spawner manager
 */
public interface SpawnerManager {

    /**
     * Returns all spawners in certain chunk
     * @param chunk Chunk to retrieve spawners for
     * @return Unmodifiable Set of {@link Spawner}s in that Chunk
     * @deprecated Use {@link SpawnerManager#getLoadedSpawnersInChunk(Chunk)}
     * for loaded stacked spawners or {@link SpawnerManager#getAllSpawnersInChunk(Chunk)} to get all stacked spawners in that chunk
     */
    @Deprecated
    Set<Spawner> getSpawnersInChunk(Chunk chunk);

    /**
     * Returns loaded stacked spawners in that chunk
     * @param chunk Chunk to retrieve stacked spawners for
     * @return Unmodifiable Set of {@link Spawner}s in that Chunk
     */
    Set<Spawner> getLoadedSpawnersInChunk(Chunk chunk);

    /**
     * Returns all stacked spawners in that chunk
     * @param chunk Chunk to retrieve stacked spawners for
     * @return Set of {@link Spawner}s in that Chunk
     */
    Set<Spawner> getAllSpawnersInChunk(Chunk chunk);

    /**
     * Checks if there are spawners for that chunk
     * @param chunk Chunk to check if it contains spawners
     * @return True if there are spawners for that chunk
     */
    boolean containsChunk(Chunk chunk);
}
