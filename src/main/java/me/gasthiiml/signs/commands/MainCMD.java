package me.gasthiiml.signs.commands;

import me.gasthiiml.signs.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class MainCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if(!player.hasPermission("kitpvp.minelatino.carteles.admin") && !player.isOp())
            return false;

        Block view = player.getTargetBlock((Set<Material>) null, 4);

        if(!(view.getType() == Material.WALL_SIGN) && !(view.getType() == Material.SIGN_POST)) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                        '&', "&4El bloque debe ser una señal."
                    )
            );

            return false;
        }

        if(Main.getInstance().getManager().getSigns().contains(view.getLocation())) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&', "&4Ya existe un registro en estas coordenadas."
                    )
            );

            return false;
        }

        Main.getInstance().getManager().getSigns().add(view.getLocation());
        Main.getInstance().getManager().save();

        player.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&', "&bSeñal añadida correctamente!"
                )
        );

        return false;
    }

}
