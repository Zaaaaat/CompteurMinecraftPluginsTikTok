package fr.zat.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private ZoneManager zoneManager;

    @Override
    public void onEnable() {
        zoneManager = new ZoneManager(this);

        // Enregistrement des commandes
        getCommand("setCorner1").setExecutor(new SetCornerCommand(this, zoneManager, 1));
        getCommand("setCorner2").setExecutor(new SetCornerCommand(this, zoneManager, 2));
        getCommand("startGame").setExecutor(new StartGameCommand(this, zoneManager));
    }

    @Override
    public void onDisable() {
        // Gestion de l'arrêt du plugin si nécessaire
    }
}
