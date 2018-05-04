package me.mtm123.factionsaddons.data;

import me.mtm123.factionsaddons.api.PlayerSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FAPlayerSettingsManager implements PlayerSettingsManager{

    private final FileConfiguration playerData;
    private final Map<UUID, FAPlayerSettings> playerSettings;

    public FAPlayerSettingsManager(FileConfiguration playerData){
        this.playerSettings = new HashMap<>();
        this.playerData = playerData;
    }

    @Override
    public FAPlayerSettings getPlayerSettings(Player player){
        return getPlayerSettings(player.getUniqueId());
    }

    @Override
    public FAPlayerSettings getPlayerSettings(UUID uuid){
        if(!playerSettings.containsKey(uuid))
            loadPlayerSettings(uuid);

        return playerSettings.get(uuid);
    }

    private void loadPlayerSettings(Player player){
        loadPlayerSettings(player.getUniqueId());
    }

    public void loadPlayerSettings(UUID uuid){
        if(!playerData.contains(uuid.toString()))
            createPlayerSettings(uuid);

        FAPlayerSettings pSettings = (FAPlayerSettings) playerData.get(uuid.toString());
        playerSettings.put(uuid, pSettings);
    }


    private void createPlayerSettings(UUID uuid){
        FAPlayerSettings pSettings = new FAPlayerSettings();
        playerSettings.put(uuid, pSettings);

        playerData.set(uuid.toString(), pSettings);
        ConfigManager.save(playerData, "playerdata.yml");
    }

    public void saveAndRemove(Player player){
        saveAndRemove(player.getUniqueId());
    }

    private void saveAndRemove(UUID uuid){
        if(playerSettings.containsKey(uuid)){
            FAPlayerSettings pSettings = playerSettings.get(uuid);

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
