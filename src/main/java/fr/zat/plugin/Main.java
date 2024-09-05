package fr.zat.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

    private final int x1 = -9, y1 = 169, z1 = 9;
    private final int x2 = -1, y2 = 169, z2 = 1;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isZoneFilled(event.getBlock().getWorld())) {
            startCountdown(event.getBlock().getWorld());
        }
    }

    private boolean isZoneFilled(World world) {
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                if (world.getBlockAt(x, y1, z).getType() == Material.AIR) {
                    return false; // Il y a encore des blocs d'air
                }
            }
        }
        return true; // La zone est remplie
    }

    private void startCountdown(World world) {
        new BukkitRunnable() {
            int countdown = 10;

            @Override
            public void run() {
                if (countdown == 0) {
                    transformZoneToObsidian(world);
                    cancel();
                } else {
                    Bukkit.broadcastMessage("Transformation dans " + countdown + " secondes...");
                    countdown--;
                }
            }
        }.runTaskTimer(this, 0, 20); // 20 ticks = 1 seconde
    }

    private void transformZoneToObsidian(World world) {
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                world.getBlockAt(x, y1, z).setType(Material.OBSIDIAN);
            }
        }
        Bukkit.broadcastMessage("La zone a été transformée en obsidienne !");
    }
}
