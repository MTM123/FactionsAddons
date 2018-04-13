package me.mtm123.factionsaddons;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerSettings implements ConfigurationSerializable{

    private boolean tntVisible;

    PlayerSettings(){
        tntVisible = true;
    }

    PlayerSettings(Map<String, Object> data){
        this.tntVisible = (boolean) data.get("tntVisible");
    }

    public boolean isTntVisible() {
        return tntVisible;
    }

    public void setTntVisible(boolean tntVisible) {
        this.tntVisible = tntVisible;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("tntVisible", tntVisible);

        return data;
    }
}
