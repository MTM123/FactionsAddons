package lv.mtm123.factionsaddons.modules;

import lv.mtm123.factionsaddons.FactionsAddons;
import lv.mtm123.spigotutils.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ItemProtectModule extends AbstractModule {

    private final Map<Material, Short> protectedMaterials;

    public ItemProtectModule(FactionsAddons plugin, FileConfiguration cfg) {
        super(plugin);
        this.protectedMaterials = new HashMap<>();

        loadMaterials(cfg);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack i = event.getEntity().getItemStack();
        if (protectedMaterials.containsKey(i.getType())) {
            short durability = protectedMaterials.get(i.getType());
            if (durability == -1 || durability == i.getDurability()) {
                makeInvulnerable(event.getEntity());
            }

        }

    }

    private void loadMaterials(FileConfiguration cfg) {
        for (String i : cfg.getStringList("modules.item-protect.protected-items")) {

            String[] itemdata = i.split(":");
            if (itemdata.length == 0) continue;

            Material mat = Material.matchMaterial(itemdata[0]);
            if (mat == null) continue;

            short durability = -1;
            if (itemdata.length == 2) {
                try {
                    durability = Short.parseShort(itemdata[1]);
                } catch (NumberFormatException ignored) {
                }
            }

            protectedMaterials.put(mat, durability);

        }
    }

    private void makeInvulnerable(Item i) {

        try {
            Class<?> cCraftEntity = ReflectionUtil.getClass(ReflectionUtil.Package.CB, "entity.CraftEntity");
            Class<?> cNmsEntity = ReflectionUtil.getClass(ReflectionUtil.Package.NMS, "Entity");

            Method getHandle = cCraftEntity.getDeclaredMethod("getHandle");
            Object nmsEntity = getHandle.invoke(i);

            Field f = cNmsEntity.getDeclaredField("invulnerable");
            f.setAccessible(true);

            f.set(nmsEntity, true);

            f.setAccessible(false);
        } catch (ClassNotFoundException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

}
