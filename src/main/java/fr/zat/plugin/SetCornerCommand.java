package fr.zat.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCornerCommand implements CommandExecutor {
    private final Main plugin;
    private final ZoneManager zoneManager;
    private final int corner;

    public SetCornerCommand(Main plugin, ZoneManager zoneManager, int corner) {
        this.plugin = plugin;
        this.zoneManager = zoneManager;
        this.corner = corner;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (corner == 1) {
                zoneManager.setCorner1(player.getLocation());
                player.sendMessage("Corner 1 set!");
            } else if (corner == 2) {
                zoneManager.setCorner2(player.getLocation());
                player.sendMessage("Corner 2 set!");
            }
            return true;
        }
        return false;
    }
}
