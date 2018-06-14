package lv.mtm123.factionsaddons.api.events;

import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.entity.Player;

public class StackedSpawnerPlaceEvent extends StackedSpawnerEvent {

    public StackedSpawnerPlaceEvent(Player player, Spawner spawner) {
        super(spawner, player);
    }

}
