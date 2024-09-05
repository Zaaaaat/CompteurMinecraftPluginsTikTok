package fr.zat.plugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCornerCommand implements CommandExecutor {
    private final Main plugin;
    private final ZoneManager zoneManager;
    private final int cornerNumber;

    public SetCornerCommand(Main plugin, ZoneManager zoneManager, int cornerNumber) {
        this.plugin = plugin;
        this.zoneManager = zoneManager;
        this.cornerNumber = cornerNumber;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();

            if (cornerNumber == 1) {
                zoneManager.setCorner1(location);
                player.sendMessage("Le premier coin de la zone a été défini.");
            } else if (cornerNumber == 2) {
                zoneManager.setCorner2(location);
                player.sendMessage("Le deuxième coin de la zone a été défini.");
            }

            return true;
        } else {
            sender.sendMessage("Seuls les joueurs peuvent exécuter cette commande.");
            return false;
        }
    }
}
