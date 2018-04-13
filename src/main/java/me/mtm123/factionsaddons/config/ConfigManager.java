package me.mtm123.factionsaddons.config;

import me.mtm123.factionsaddons.FactionsAddons;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class ConfigManager {

    public static FileConfiguration load(String filename){

        File file = new File(FactionsAddons.getInstance().getDataFolder(), filename);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        try {
            if(!file.exists()){

                InputStream in = FactionsAddons.getInstance().getResource(filename);
                if(in != null){
                    cfg.load(in);
                }

            }
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return cfg;
    }

    public static void save(FileConfiguration config, String filename){

        File file = new File(FactionsAddons.getInstance().getDataFolder(), filename);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
