package lv.mtm123.factionsaddons.api.events;


import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class StackedSpawnerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Spawner spawner;
    private final Entity entity;
    private final ChangingCause cause;

    StackedSpawnerEvent(Spawner spawner, Entity entity) {
        this.spawner = spawner;
        this.entity = entity;

        this.cause = entity instanceof Player ? ChangingCause.PLAYER : ChangingCause.EXPLOSION;
    }

    public enum ChangingCause{
        PLAYER,
        EXPLOSION
    }

    /**
     * Returns spawner involved in this event
     * @return Involved spawner
     */
    public Spawner getSpawner() {
        return spawner;
    }

    /**
     * Returns entity involved in this event
     * @return Involved entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Returns cause of this event
     * @return Cause of this event
     */
    public ChangingCause getCause(){
        return cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
