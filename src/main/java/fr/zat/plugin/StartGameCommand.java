package fr.zat.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGameCommand implements CommandExecutor {
    private final Main plugin;
    private final ZoneManager zoneManager;

    public StartGameCommand(Main plugin, ZoneManager zoneManager) {
        this.plugin = plugin;
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (zoneManager.isCornersSet()) {
            zoneManager.startGame();
            sender.sendMessage("Game started!");
        } else {
            sender.sendMessage("Please set both corners first!");
        }
        return true;
    }
}
