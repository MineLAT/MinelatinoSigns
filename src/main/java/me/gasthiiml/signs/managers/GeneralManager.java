package me.gasthiiml.signs.managers;

import lombok.Getter;
import me.gasthiiml.signs.Main;
import me.gasthiiml.signs.utils.LocationUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class GeneralManager {

    private final Main plugin;
    private final FileConfiguration config;

    private final List<Material> blacklist;
    @Getter
    private final List<Location> signs;

    @Getter
    private Economy econ = null;

    public GeneralManager(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.blacklist = new ArrayList<>();
        this.signs = new ArrayList<>();

        if (!setupEconomy() ) {
            plugin.getLogger().severe("Disabled due to no Vault dependency found!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        load();
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider();
        return econ != null;
    }

    private void load() {
        if(config.contains("signs")) {
            config.getStringList("signs").forEach(key -> {
                Location loc = LocationUtils.locationFromConfig(key);

                if(loc == null)
                    return;

                if(!loc.getBlock().getType().equals(Material.SIGN_POST) && !loc.getBlock().getType().equals(Material.WALL_SIGN))
                    return;

                this.signs.add(loc);
            });
        }

        if(config.contains("repair-blacklist")) {
            config.getStringList("repair-blacklist").forEach(key -> {
                Material material = Material.matchMaterial(key);

                if(material != null)
                    this.blacklist.add(material);
            });
        }
    }

    public void repairAll(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if(item != null && item.getType() != Material.AIR)
                repairItem(item);
        }

        for (ItemStack content : player.getInventory().getContents()) {
            if(content != null && content.getType() != Material.AIR)
                repairItem(content);
        }

        player.updateInventory();
    }

    private void repairItem(ItemStack item) {
        final Material material = item.getType();

        if(blacklist.contains(material))
            return;

        if (material.isBlock() || material.getMaxDurability() < 1)
            return;

        if (item.getDurability() == 0)
            return;

        item.setDurability((short) 0);
    }

    public void save() {
        config.set("signs", signs.stream().map(LocationUtils::serializeLocation).toArray());
        Main.getInstance().saveConfig();
        Main.getInstance().reloadConfig();
    }

}
