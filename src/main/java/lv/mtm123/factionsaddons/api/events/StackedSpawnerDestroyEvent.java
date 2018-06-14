package lv.mtm123.factionsaddons.api.events;

import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.entity.Entity;

public class StackedSpawnerDestroyEvent extends StackedSpawnerEvent {

    public StackedSpawnerDestroyEvent(Spawner spawner, Entity entity) {
        super(spawner, entity);
    }

}
