package me.mtm123.factionsaddons.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.mtm123.factionsaddons.FactionsAddons;
import me.mtm123.factionsaddons.PlayerSettingsManager;

public class PacketListener extends PacketAdapter {

    private final PlayerSettingsManager pSettingsManager;

    public PacketListener(FactionsAddons plugin, PlayerSettingsManager pSettingsManager) {
        super(plugin, PacketType.Play.Server.SPAWN_ENTITY);
        this.pSettingsManager = pSettingsManager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {

        if(event.getPacketType().equals(PacketType.Play.Server.SPAWN_ENTITY)){

            if(!pSettingsManager.getPlayerSettings(event.getPlayer()).isTntVisible()){
                int id = event.getPacket().getIntegers().read(9);
                if(id == 50) event.setCancelled(true);
            }

        }

    }
}
