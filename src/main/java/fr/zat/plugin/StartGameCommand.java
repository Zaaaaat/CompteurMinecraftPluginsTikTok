package fr.zat.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartGameCommand implements CommandExecutor {
    private final Main plugin;
    private final ZoneManager zoneManager;

    public StartGameCommand(Main plugin, ZoneManager zoneManager) {
        this.plugin = plugin;
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (zoneManager.isCornersSet()) {
                zoneManager.startGame();
                player.sendMessage("Le jeu a commencé !");
            } else {
                player.sendMessage("Les coins de la zone ne sont pas encore définis.");
            }

            return true;
        } else {
            sender.sendMessage("Seuls les joueurs peuvent exécuter cette commande.");
            return false;
        }
    }
}
