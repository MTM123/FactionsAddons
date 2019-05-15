package lv.mtm123.factionsaddons.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.data.FAPlayerSettingsManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PacketListener extends PacketAdapter {

    private final FAPlayerSettingsManager pSettingsManager;

    public PacketListener(FactionsAddons plugin, FileConfiguration cfg, FAPlayerSettingsManager pSettingsManager) {
        super(plugin, getPacketTypes(cfg));
        this.pSettingsManager = pSettingsManager;
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event) {

        if (!pSettingsManager.getPlayerSettings(event.getPlayer()).isTNTVisible()) {

            if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {

                int id = event.getPacket().getIntegers().read(9);
                if (id == 50)
                    event.setCancelled(true);

            } else if (event.getPacketType() == PacketType.Play.Server.EXPLOSION) {

                event.setCancelled(true);

            }

        }

    }

    private static PacketType[] getPacketTypes(FileConfiguration cfg) {
        List<PacketType> packetTypes = new ArrayList<>();
        packetTypes.add(PacketType.Play.Server.SPAWN_ENTITY);

        if (cfg.getBoolean("modules.toggletnt.disable-particles-and-sound")) {
            packetTypes.add(PacketType.Play.Server.EXPLOSION);
        }

        return packetTypes.toArray(new PacketType[]{});
    }
}
