package lv.mtm123.factionsaddons.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public final class StringUtil {

    private StringUtil(){}

    public static String locToChunkKey(Chunk chunk){
        return String.valueOf(chunk.getX()) + "_" + String.valueOf(chunk.getZ());
    }

    public static String locToLocKey(Location loc){
        return String.valueOf(loc.getBlockX()) + "_" + String.valueOf(loc.getBlockY()) + "_" + String.valueOf(loc.getBlockZ());
    }

    public static Location keyToLoc(World world, String key){

        String[] coords = key.split("_");

        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        int z = Integer.parseInt(coords[2]);

        return new Location(world, x, y, z);
    }

    public static String getNormalizedSpawnerName(EntityType type){
        String displayName = type.getName();
        if(type == EntityType.IRON_GOLEM){
            displayName = displayName.replace("Villager" , "Iron");
        }else if(type == EntityType.PIG_ZOMBIE){
            displayName = displayName.replace("PigZombie", "ZombiePigman");
        }

        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(displayName), " ") + " Spawner";
    }

}
