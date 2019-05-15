package lv.mtm123.factionsaddons.api;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents player settings manager
 */
public interface PlayerSettingsManager {

    /**
     * Retrieves player settings
     *
     * @param player Player for whom to retrieve settings
     * @return Player settings
     */
    PlayerSettings getPlayerSettings(Player player);

    /**
     * Retrieves player settings
     *
     * @param uuid UUID of player for whom to retrieve settings
     * @return Player settings
     */
    PlayerSettings getPlayerSettings(UUID uuid);

}
