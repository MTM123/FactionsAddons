package me.mtm123.factionsaddons;

import me.mtm123.factionsaddons.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSettingsManager {

    private final FileConfiguration playerData;
    private final Map<UUID, PlayerSettings> playerSettings;

    public PlayerSettingsManager(FileConfiguration playerData){
        this.playerSettings = new HashMap<>();
        this.playerData = playerData;
    }

    public PlayerSettings getPlayerSettings(Player player){
        if(!playerSettings.containsKey(player.getUniqueId()))
            loadPlayerSettings(player);

        return playerSettings.get(player.getUniqueId());
    }

    public void loadPlayerSettings(Player player){
        loadPlayerSettings(player.getUniqueId());
    }

    public void loadPlayerSettings(UUID uuid){
        if(!playerData.contains(uuid.toString()))
            createPlayerSettings(uuid);

        PlayerSettings pSettings = (PlayerSettings) playerData.get(uuid.toString());
        playerSettings.put(uuid, pSettings);
    }


    private void createPlayerSettings(UUID uuid){
        PlayerSettings pSettings = new PlayerSettings();
        playerSettings.put(uuid, pSettings);

        playerData.set(uuid.toString(), pSettings);
        ConfigManager.save(playerData, "playerdata.yml");
    }

    public void saveAndRemove(Player player){
        saveAndRemove(player.getUniqueId());
    }

    public void saveAndRemove(UUID uuid){
        if(playerSettings.containsKey(uuid)){
            PlayerSettings pSettings = playerSettings.get(uuid);

            playerData.set(uuid.toString(), pSettings);
            ConfigManager.save(playerData, "playerdata.yml");

            playerSettings.remove(uuid);
        }
    }

    public void saveAllAndRemove(){
        for(Player player : Bukkit.getOnlinePlayers()){
            saveAndRemove(player);
        }
    }

    public void loadAllOnlinePlayerSettings(){
        for(Player player : Bukkit.getOnlinePlayers()){
            loadPlayerSettings(player);
        }
    }

}
