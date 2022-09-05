package me.gasthiiml.signs;

import lombok.Getter;
import me.gasthiiml.signs.commands.MainCMD;
import me.gasthiiml.signs.listeners.GeneralListener;
import me.gasthiiml.signs.managers.GeneralManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    private GeneralManager manager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.manager = new GeneralManager(this);

        getCommand("agregarcartel").setExecutor(new MainCMD());
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
    }

}
