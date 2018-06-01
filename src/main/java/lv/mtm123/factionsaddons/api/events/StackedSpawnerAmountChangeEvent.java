package lv.mtm123.factionsaddons.api.events;

import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.entity.Entity;


public class StackedSpawnerAmountChangeEvent extends StackedSpawnerEvent {

    private final Entity entity;
    private final ChangingCause changingCause;
    private final int from;
    private final int to;

    public StackedSpawnerAmountChangeEvent(Spawner spawner, Entity entity, ChangingCause changingCause, int from, int to) {
        super(spawner);
        this.entity = entity;
        this.changingCause = changingCause;
        this.from = from;
        this.to = to;
    }

    public Entity getEntity() {
        return entity;
    }

    public ChangingCause getChangingCause() {
        return changingCause;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
