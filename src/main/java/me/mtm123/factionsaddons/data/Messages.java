package me.mtm123.factionsaddons.data;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {

    private final String prefix;
    private final FileConfiguration msgs;

    public Messages(FileConfiguration msgs){
        this.msgs = msgs;
        this.prefix = msgs.getString("prefix");
    }

    public String get(String msgKey){
        return ChatColor.translateAlternateColorCodes('&', prefix + msgs.getString(msgKey));
    }

    public String getFormat(String formatKey){
        return ChatColor.translateAlternateColorCodes('&', msgs.getString(formatKey));
    }
}
