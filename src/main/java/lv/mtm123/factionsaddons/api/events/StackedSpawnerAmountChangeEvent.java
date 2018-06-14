package lv.mtm123.factionsaddons.api.events;

import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.entity.Entity;


public class StackedSpawnerAmountChangeEvent extends StackedSpawnerEvent {

    private final int from;
    private final int to;

    public StackedSpawnerAmountChangeEvent(Spawner spawner, Entity entity, int from, int to) {
        super(spawner, entity);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the amount before the change happened
     * @return Spawner amount
     */
    public int getFrom() {
        return from;
    }

    /**
     * Returns the amount after the change happened
     * @return Spawner amount
     */
    public int getTo() {
        return to;
    }
}
