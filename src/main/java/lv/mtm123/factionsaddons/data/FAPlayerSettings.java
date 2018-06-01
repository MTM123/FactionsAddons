package lv.mtm123.factionsaddons.data;

import lv.mtm123.factionsaddons.api.PlayerSettings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class FAPlayerSettings implements PlayerSettings, ConfigurationSerializable{

    private boolean tntVisible;
    private long enderpearlCooldownUntil;

    FAPlayerSettings(){
        tntVisible = true;
        enderpearlCooldownUntil = 0;
    }

    FAPlayerSettings(Map<String, Object> data){
        this.tntVisible = (boolean) data.get("tntVisible");
        this.enderpearlCooldownUntil = (long) data.get("enderpearlCooldownUntil");
    }

    @Override
    public boolean isTNTVisible() {
        return tntVisible;
    }

    @Override
    public void setTNTVisible(boolean tntVisible) {
        this.tntVisible = tntVisible;
    }

    @Override
    public boolean hasEnderpearlCooldown() {
        return enderpearlCooldownUntil >= System.currentTimeMillis();
    }

    @Override
    public long getEnderpearlCooldownLeft(){
        return enderpearlCooldownUntil - System.currentTimeMillis();
    }

    @Override
    public void setEnderpearlCooldownUntil(long enderpearlCooldownUntil){
        this.enderpearlCooldownUntil = enderpearlCooldownUntil;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("tntVisible", tntVisible);
        data.put("enderpearlCooldownUntil", enderpearlCooldownUntil);

        return data;
    }
}
