package lv.mtm123.factionsaddons;

import co.aikar.commands.BukkitCommandManager;
import de.dustplanet.util.SilkUtil;
import lv.mtm123.factionsaddons.api.FactionsAddonsAPI;
import lv.mtm123.factionsaddons.api.Module;
import lv.mtm123.factionsaddons.api.PlayerSettingsManager;
import lv.mtm123.factionsaddons.api.SpawnerManager;
import lv.mtm123.factionsaddons.commands.CoreCommand;
import lv.mtm123.factionsaddons.commands.ModuleCommands;
import lv.mtm123.factionsaddons.data.*;
import lv.mtm123.factionsaddons.hooks.SilkSpawnersHook;
import lv.mtm123.factionsaddons.listeners.PacketListener;
import lv.mtm123.factionsaddons.listeners.PlayerLoginStateListener;
import lv.mtm123.factionsaddons.modules.EnderpearlModule;
import lv.mtm123.factionsaddons.modules.HarvestingHoeModule;
import lv.mtm123.factionsaddons.modules.ItemProtectModule;
import lv.mtm123.factionsaddons.modules.SpawnerModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@Main
@Name("FactionsAddons")
@Author("MTM123")
@Version("0.2.0")
@Description("Adds different addons for factions")
@SoftDependsOn({"ProtocolLib", "SilkSpawners"})
public class FactionsAddons extends JavaPlugin {

    private static FactionsAddons plugin;
    private static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&8[&6FactionsAddons&8] ");

    private FileConfiguration cfg;
    private FileConfiguration playerData;
    private FileConfiguration spawnerCfg;
    private FileConfiguration msgsCfg;

    private FAPlayerSettingsManager pSettingsManager;
    private FASpawnerManager spawnerManager;
    private Messages msgs;

    private Set<Module> enabledModules;
    private FactionsAddonsAPI api;

    @Override
    public void onEnable() {
        plugin = this;

        ConfigurationSerialization.registerClass(FAPlayerSettings.class);
        loadPlugin(false);

        //bStats
        Metrics metrics = new Metrics(this);
    }

    @Override
    public void onDisable() {
        pSettingsManager.saveAllAndRemove();
        spawnerManager.saveAllAndUnload();
        ConfigManager.save(playerData, "playerdata.yml");
        ConfigManager.save(spawnerCfg, "spawners.yml");
    }


    public void loadPlugin(boolean reload) {

        if (reload) {
            pSettingsManager.saveAllAndRemove();
            spawnerManager.saveAllAndUnload();
            HandlerList.unregisterAll(this);
        }

        enabledModules = new HashSet<>();

        cfg = ConfigManager.load("config.yml");
        playerData = ConfigManager.load("playerdata.yml");
        spawnerCfg = ConfigManager.load("spawners.yml");
        msgsCfg = ConfigManager.load("messages.yml");

        msgs = new Messages(msgsCfg);

        pSettingsManager = new FAPlayerSettingsManager(playerData);
        pSettingsManager.loadAllOnlinePlayerSettings();

        registerCommands();
        registerGlobalListeners();

        loadModules();

        api = new FactionsAddonsAPI() {
            @Override
            public SpawnerManager getSpawnerManager() {
                return spawnerManager;
            }

            @Override
            public PlayerSettingsManager getPlayerSettingsManager() {
                return pSettingsManager;
            }

            @Override
            public boolean isModuleEnabled(Module module) {
                return enabledModules.contains(module);
            }
        };
    }

    private void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);

        commandManager.enableUnstableAPI("help");

        commandManager.registerDependency(String.class, PREFIX);
        commandManager.registerDependency(FAPlayerSettingsManager.class, pSettingsManager);
        commandManager.registerDependency(Messages.class, msgs);
        commandManager.registerDependency(FileConfiguration.class, cfg);

        commandManager.registerCommand(new CoreCommand());
        commandManager.registerCommand(new ModuleCommands());
    }

    private void registerGlobalListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerLoginStateListener(pSettingsManager), this);
    }

    private void loadModules() {

        for (String key : cfg.getConfigurationSection("modules").getKeys(false)) {
            if (cfg.getBoolean("modules." + key + ".enabled")) {
                switch (key) {
                    case "toggletnt":
                        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
                            getLogger().log(Level.WARNING, "ProtocolLib missing! ToggleTNT module won't work!");
                            break;
                        }

                        new PacketListener(this, cfg, pSettingsManager);
                        enabledModules.add(Module.TOGGLE_TNT);
                        break;
                    case "item-protect":
                        new ItemProtectModule(this, cfg);
                        enabledModules.add(Module.ITEM_PROTECT);
                        break;
                    case "harvesting-hoe":
                        new HarvestingHoeModule(this, msgs, cfg);
                        enabledModules.add(Module.HARVESTING_HOE);
                        break;
                    case "spawners":
                        spawnerManager = new FASpawnerManager(this, msgs, cfg, spawnerCfg);

                        boolean silkSpawnersEnabled = getServer()
                                .getPluginManager().isPluginEnabled("SilkSpawners");

                        if (silkSpawnersEnabled) {
                            SilkUtil su = SilkUtil.hookIntoSilkSpanwers();
                            new SilkSpawnersHook(this, spawnerManager, msgs, su, cfg);
                            getLogger().log(Level.INFO, "Hooked with SilkSpawners!");
                        }

                        new SpawnerModule(this, spawnerManager, msgs, cfg, silkSpawnersEnabled);
                        for (World w : getServer().getWorlds()) {
                            for (Chunk c : w.getLoadedChunks()) {
                                spawnerManager.loadSpawnersInChunk(c);
                            }
                        }

                        enabledModules.add(Module.SPAWNERS);
                        break;
                    case "enderpearls":
                        new EnderpearlModule(this, pSettingsManager, msgs, cfg);
                        enabledModules.add(Module.ENDERPEARL);
                        break;
                }
            }
        }

    }

    public boolean isModuleEnabled(Module module) {
        return enabledModules.contains(module);
    }

    public Set<Module> getEnabledModules() {
        return enabledModules;
    }

    public static FactionsAddons getInstance() {
        return plugin;
    }

    public FactionsAddonsAPI getAPI() {
        return api;
    }

}