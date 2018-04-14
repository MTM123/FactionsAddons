package me.mtm123.factionsaddons;

import co.aikar.commands.BukkitCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.mtm123.factionsaddons.commands.CoreCommand;
import me.mtm123.factionsaddons.config.ConfigManager;
import me.mtm123.factionsaddons.listeners.PacketListener;
import me.mtm123.factionsaddons.listeners.player.PlayerLoginStateListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.*;

import java.util.logging.Level;

@Main
@Name("FactionsAddons")
@Author("MTM123")
@Version("0.1.0")
@Description("Adds different addons for factions")
@DependsOn("ProtocolLib")
public class FactionsAddons extends JavaPlugin {

    private static FactionsAddons plugin;
    private static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&8[&6FactionsAddons&8] ");

    private FileConfiguration playerData;
    private PlayerSettingsManager pSettingsManager;

    private ProtocolManager protocolManager;

    @Override
    public void onEnable(){
        plugin = this;

        if(!getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            getLogger().log(Level.SEVERE, "ProtocolLib missing! Please install it to use this plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ConfigurationSerialization.registerClass(PlayerSettings.class);
        loadPlugin(false);

        //bStats
        Metrics metrics = new Metrics(this);
    }

    @Override
    public void onDisable(){
        ConfigManager.save(playerData, "playerdata.yml");
    }


    public void loadPlugin(boolean reload){

        if(reload){
            pSettingsManager.saveAllAndRemove();
        }

        playerData = ConfigManager.load("playerdata.yml");

        pSettingsManager = new PlayerSettingsManager(playerData);
        pSettingsManager.loadAllOnlinePlayerSettings();

        if(!reload){
            protocolManager = ProtocolLibrary.getProtocolManager();
        }

        registerCommands();
        registerListeners();
    }

    private void registerCommands(){
        BukkitCommandManager commandManager = new BukkitCommandManager(this);

        commandManager.registerDependency(String.class, PREFIX);
        commandManager.registerDependency(PlayerSettingsManager.class, pSettingsManager);

        commandManager.registerCommand(new CoreCommand());
    }

    private void registerListeners(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerLoginStateListener(pSettingsManager), this);

        //ProtocolLib
        protocolManager.addPacketListener(new PacketListener(this, pSettingsManager));
    }

    public static FactionsAddons getInstance() {
        return plugin;
    }
}
