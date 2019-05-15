package lv.mtm123.factionsaddons.modules;

import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.factionsaddons.data.FAPlayerSettings;
import lv.mtm123.factionsaddons.data.FAPlayerSettingsManager;
import lv.mtm123.factionsaddons.data.Messages;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderpearlModule extends AbstractModule {

    private final FAPlayerSettingsManager pSettingsManager;
    private final String cooldownMsg;
    private final long cooldownInMs;

    public EnderpearlModule(FactionsAddons plugin, FAPlayerSettingsManager pSettingsManager, Messages msgs, FileConfiguration cfg) {
        super(plugin);
        cooldownInMs = Math.round(cfg.getDouble("modules.enderpearls.cooldown-in-seconds") * 1000);
        this.pSettingsManager = pSettingsManager;
        this.cooldownMsg = msgs.get("enderpearl-cooldown");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_AIR))
            return;

        if (event.getItem() == null || event.getItem().getType() != Material.ENDER_PEARL)
            return;

        Player player = event.getPlayer();
        if (!player.hasPermission("fa.enderpearl.bypass")) {

            FAPlayerSettings pSettings = pSettingsManager.getPlayerSettings(player);
            if (pSettings.hasEnderpearlCooldown()) {
                player.sendMessage(constructCooldownMessage(pSettings.getEnderpearlCooldownLeft()));
                event.setCancelled(true);
            } else {
                pSettings.setEnderpearlCooldownUntil(cooldownInMs + System.currentTimeMillis());
            }

        }

    }

    private String constructCooldownMessage(long cooldownLeft) {
        return cooldownMsg.replace("%time%", String.valueOf(Math.round(cooldownLeft / (double) 1000)));
    }

}
