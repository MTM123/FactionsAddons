package lv.mtm123.factionsaddons.api.events;

import lv.mtm123.factionsaddons.api.Spawner;
import org.bukkit.entity.Player;

public class StackedSpawnerCreateEvent extends StackedSpawnerEvent {

    private final Player player;

    public StackedSpawnerCreateEvent(Player player, Spawner spawner) {
        super(spawner);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
