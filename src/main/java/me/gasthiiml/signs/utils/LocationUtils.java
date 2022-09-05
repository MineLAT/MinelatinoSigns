package me.gasthiiml.signs.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@UtilityClass
public class LocationUtils {

    public Location locationFromConfig(String config) {
        String[] parts = config.split(";");

        World world = Bukkit.getWorld(parts[0]);

        if(world == null)
            return null;

        return new Location(
                world,
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3])
        );
    }

    public String serializeLocation(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ();
    }

}
