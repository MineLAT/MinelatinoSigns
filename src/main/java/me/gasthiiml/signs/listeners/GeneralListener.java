package me.gasthiiml.signs.listeners;

import me.gasthiiml.signs.Main;
import me.gasthiiml.signs.managers.GeneralManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GeneralListener implements Listener {

    private final ConcurrentHashMap<UUID, Long> cooldown = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        FileConfiguration config = Main.getInstance().getConfig();
        GeneralManager manager = Main.getInstance().getManager();

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
                Sign sign = (Sign) e.getClickedBlock().getState();

                if(manager.getSigns().contains(sign.getLocation())) {
                    if(cooldown.containsKey(player.getUniqueId()) && getDifference(player) >= 0) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes(
                                        '&', config.getString("cooldown-message")
                                                .replaceAll("<time>", TimeUnit.MILLISECONDS.toSeconds(getDifference(player)) + "")
                                )
                        );
                        return;
                    }

                    if(!manager.getEcon().has(player, config.getInt("required-coins", 5))) {
                        sendError(player);
                        return;
                    }

                    if(!manager.getEcon().withdrawPlayer(player, config.getInt("required-coins", 5)).transactionSuccess()) {
                        sendError(player);
                        return;
                    }

                    cooldown.put(player.getUniqueId(), (System.currentTimeMillis() + (config.getInt("cooldown", 1) * 1000L)));
                    manager.repairAll(player);

                    player.sendMessage(
                            ChatColor.translateAlternateColorCodes(
                                    '&', config.getString("success-message")
                                            .replaceAll("<amount>", manager.getEcon().getBalance(player) + "")
                            )
                    );
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        String[] lineArray = e.getLines();
        int lineArrayLength = lineArray.length;

        for (int i = 0; i < lineArrayLength; i++) {
            String oldLine = lineArray[i];
            e.setLine(i, ChatColor.translateAlternateColorCodes('&', oldLine));
        }
    }

    private void sendError(Player player) {
        FileConfiguration config = Main.getInstance().getConfig();

        if(config.contains("error-message") && !config.getString("error-message").isEmpty())
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("error-message")));
    }

    private long getDifference(Player player) {
        if(this.cooldown.containsKey(player.getUniqueId()))
            return this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis();

        return -1;
    }
}
