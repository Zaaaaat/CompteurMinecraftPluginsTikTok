package fr.zat.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetGameCommand implements CommandExecutor {
    private final Main plugin;
    private final ZoneManager zoneManager;

    public ResetGameCommand(Main plugin, ZoneManager zoneManager) {
        this.plugin = plugin;
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            zoneManager.stopGame();
            zoneManager.setCorner1(null);
            zoneManager.setCorner2(null);
            player.sendMessage("Le jeu a été arrêté et les coordonnées de la zone ont été réinitialisées.");
            return true;
        } else {
            sender.sendMessage("Seuls les joueurs peuvent exécuter cette commande.");
            return false;
        }
    }
}
