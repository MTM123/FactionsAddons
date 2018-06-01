package lv.mtm123.factionsaddons.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.data.Messages;
import lv.mtm123.factionsaddons.data.FAPlayerSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.stream.Collectors;

@CommandAlias("factionsaddons|fa|faddons|fadd")
public class CoreCommand extends BaseCommand {

    @Dependency
    private FactionsAddons plugin;

    @Dependency
    private FAPlayerSettingsManager pSettingsManager;

    @Dependency
    private Messages msgs;

    @Dependency
    private FileConfiguration cfg;

    @Subcommand("reload")
    @CommandAlias("far|fareload|factionsaddonsreload")
    @CommandPermission("fa.reload")
    @Description("Reloads the plugin")
    public void onReload(CommandSender sender){

        plugin.loadPlugin(true);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6FactionsAddons&8]&6>> &aPlugin reloaded!"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6FactionsAddons&8]&6>> &aEnabled modules"));
        sender.sendMessage(ChatColor.GRAY + String.join(", ", plugin.getEnabledModules().stream().map(Enum::name).collect(Collectors.toSet())));

    }

    @Subcommand("version")
    @CommandAlias("fav|faver|faversion|factionsaddonsversion")
    @CommandPermission("fa.version")
    @Description("Shows current plugin version")
    public void onVersion(CommandSender sender){

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&6FactionsAddons&8]&6>> &7Current version: v"  + plugin.getDescription().getVersion()));

    }

    @HelpCommand
    @CommandPermission("fa.help")
    @Description("Shows command help information")
    public void onHelp(CommandSender sender, CommandHelp help){
        help.showHelp();
    }

}
