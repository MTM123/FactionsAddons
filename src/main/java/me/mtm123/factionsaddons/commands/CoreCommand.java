package me.mtm123.factionsaddons.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import me.mtm123.factionsaddons.FactionsAddons;
import me.mtm123.factionsaddons.PlayerSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("factionsaddons|fa|faddons|fadd")
public class CoreCommand extends BaseCommand {

    @Dependency
    private String PREFIX;

    @Dependency
    private FactionsAddons plugin;

    @Dependency
    private PlayerSettingsManager pSettingsManager;

    @Subcommand("toggletnt")
    @CommandAlias("tt|toggletnt|ttnt")
    @CommandPermission("fa.toggletnt")
    public void onToggleTnt(Player player){

        if(pSettingsManager.getPlayerSettings(player).isTntVisible()){
            pSettingsManager.getPlayerSettings(player).setTntVisible(false);
            player.sendMessage(PREFIX + "&cTNT is now hidden!".replace('&', ChatColor.COLOR_CHAR));
        }else{
            pSettingsManager.getPlayerSettings(player).setTntVisible(true);
            player.sendMessage(PREFIX + "&aTNT is now visible!".replace('&', ChatColor.COLOR_CHAR));
        }

    }

    @Subcommand("reload")
    @CommandAlias("far|fareload|factionsaddonsreload")
    @CommandPermission("fa.reload")
    public void onReload(CommandSender sender){

        plugin.loadPlugin(true);
        sender.sendMessage(PREFIX + "&aPlugin reloaded!".replace('&', ChatColor.COLOR_CHAR));

    }

    @Subcommand("version")
    @CommandAlias("fav|faver|faversion|factionsaddonsversion")
    @CommandPermission("fa.version")
    public void onVersion(CommandSender sender){
        sender.sendMessage(PREFIX
                + ChatColor.translateAlternateColorCodes('&',
                "&7Current version: v"  + plugin.getDescription().getVersion()));
    }


}
