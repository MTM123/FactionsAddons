package lv.mtm123.factionsaddons.api.events;


import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class StackedSpawnerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Spawner spawner;

    public StackedSpawnerEvent(Spawner spawner) {
        this.spawner = spawner;
    }

    public enum ChangingCause{
        PLAYER,
        EXPLOSION
    }

    public Spawner getSpawner() {
        return spawner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
