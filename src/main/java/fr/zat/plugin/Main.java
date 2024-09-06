package fr.zat.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private ZoneManager zoneManager;

    @Override
    public void onEnable() {
        this.zoneManager = new ZoneManager(this);

        getCommand("setcorner1").setExecutor(new SetCornerCommand(this, zoneManager, 1));
        getCommand("setcorner2").setExecutor(new SetCornerCommand(this, zoneManager, 2));
        getCommand("startgame").setExecutor(new StartGameCommand(this, zoneManager));
        getCommand("resetGame").setExecutor(new ResetGameCommand(this, zoneManager));

        getServer().getPluginManager().registerEvents(new BlockListener(zoneManager), this);
    }

    @Override
    public void onDisable() {
        zoneManager.stopGame();
    }
}
