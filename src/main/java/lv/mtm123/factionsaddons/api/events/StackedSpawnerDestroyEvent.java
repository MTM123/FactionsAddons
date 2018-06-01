package lv.mtm123.factionsaddons.api.events;

import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.entity.Entity;

public class StackedSpawnerDestroyEvent extends StackedSpawnerEvent {

    private final ChangingCause destructionCause;
    private final Entity entity;

    public StackedSpawnerDestroyEvent(Spawner spawner, Entity entity, ChangingCause destructionCause) {
        super(spawner);
        this.destructionCause = destructionCause;
        this.entity = entity;
    }

    public ChangingCause getDestructionCause() {
        return destructionCause;
    }

    public Entity getEntity() {
        return entity;
    }
}
