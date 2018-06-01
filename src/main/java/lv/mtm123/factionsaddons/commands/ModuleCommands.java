package lv.mtm123.factionsaddons.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.api.Module;
import lv.mtm123.factionsaddons.data.FAPlayerSettingsManager;
import lv.mtm123.factionsaddons.data.Messages;
import lv.mtm123.factionsaddons.modules.HarvestingHoeModule;
import lv.mtm123.spigotutils.InvUtil;
import lv.mtm123.spigotutils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@CommandAlias("factionsaddons|fa|faddons|fadd")
public class ModuleCommands extends BaseCommand {

    @Dependency
    private FactionsAddons plugin;

    @Dependency
    private FAPlayerSettingsManager pSettingsManager;

    @Dependency
    private Messages msgs;

    @Dependency
    private FileConfiguration cfg;

    @Subcommand("toggletnt")
    @CommandAlias("tt|toggletnt|ttnt")
    @CommandPermission("fa.toggletnt")
    @Description("Toggles visibility of TNT")
    public void onToggleTnt(Player player){

        if(pSettingsManager.getPlayerSettings(player).isTNTVisible()){
            pSettingsManager.getPlayerSettings(player).setTNTVisible(false);
            player.sendMessage(msgs.get("commands.toggle-tnt-off"));
        }else{
            pSettingsManager.getPlayerSettings(player).setTNTVisible(true);
            player.sendMessage(msgs.get("commands.toggle-tnt-on"));
        }

    }

    @Subcommand("give")
    @CommandAlias("fagi|fagive|factionsaddonsgive")
    @CommandPermission("fa.give")
    @Description("Gives different module related items to player")
    public void onGive(CommandSender sender, @Optional String target, @Default("hh") String item, @Flags("min=1,max=64") @Default("1") int amount){

        if(plugin.isModuleEnabled(Module.HARVESTING_HOE)){
            if(sender instanceof Player && target == null){
                target = sender.getName();
            }

            if(target != null){
                Player targetPlayer = Bukkit.getPlayer(target);
                if(targetPlayer != null && targetPlayer.isOnline()){
                    ItemStack i = getHarvestingHoe(amount);
                    if(InvUtil.getFreeSpaceForItem(targetPlayer, i) > 0){
                        targetPlayer.getInventory().addItem(i);
                        sender.sendMessage(msgs.get("commands.give-given"));
                    }else{
                        if(sender.getName().equals(target)){
                            sender.sendMessage(msgs.get("commands.give-inv-full"));
                        }else{
                            /*This is done purely for compatability for plugins executing external
                             commands and not checking inventory space. For example, crate plugins*/
                            targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), i);
                            sender.sendMessage(msgs.get("commands.give-given"));
                        }
                    }
                }else{
                    sender.sendMessage(msgs.get("commands.must-be-online"));
                }
            }else{
                sender.sendMessage(msgs.get("commands.must-provide-playername"));
            }
        }else{
            sender.sendMessage(msgs.get("commands.module-disabled"));
        }


    }

    private ItemStack getHarvestingHoe(int amount){
        ItemBuilder ib = new ItemBuilder();
        String[] data = cfg.getString("modules.harvesting-hoe.item.id").split(":");

        ib.withMat(Material.matchMaterial(data[0]));
        if(data.length == 2){
            short dur = Short.parseShort(data[1]);
            ib.withData(dur);
        }

        ib.withName(cfg.getString("modules.harvesting-hoe.item.name"));

        List<String> lore = cfg.getStringList("modules.harvesting-hoe.item.lore");
        lore.add(HarvestingHoeModule.getLoreId());
        ib.withLore(lore);

        ib.setUnbreakable(cfg.getBoolean("modules.harvesting-hoe.item.unbreakable"));

        if(cfg.getBoolean("modules.harvesting-hoe.item.glowing")){
            ib.addEnchant(Enchantment.ARROW_INFINITE, 1);
            ib.addFlags(ItemFlag.HIDE_ENCHANTS);
        }

        ib.withAmount(amount);

        return ib.build();

    }

}
