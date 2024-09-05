package fr.zat.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ZoneManager {
    private final Main plugin;
    private Location corner1;
    private Location corner2;
    private boolean gameRunning = false;
    private BukkitTask zoneCheckTask;
    private BukkitTask obsidianTransformTask;

    public ZoneManager(Main plugin) {
        this.plugin = plugin;
    }

    public void setCorner1(Location loc) {
        this.corner1 = loc;
    }

    public void setCorner2(Location loc) {
        this.corner2 = loc;
    }

    public boolean isCornersSet() {
        return corner1 != null && corner2 != null;
    }

    public void startGame() {
        if (isCornersSet() && !gameRunning) {
            gameRunning = true;

            zoneCheckTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!gameRunning) return;

                    World world = corner1.getWorld();
                    int x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
                    int x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
                    int y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
                    int y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
                    int z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
                    int z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

                    boolean isComplete = true;

                    for (int x = x1; x <= x2; x++) {
                        for (int y = y1; y <= y2; y++) {
                            for (int z = z1; z <= z2; z++) {
                                Block block = world.getBlockAt(x, y, z);
                                if (block.getType().isAir()) {
                                    isComplete = false;
                                    break;
                                }
                            }
                        }
                    }

                    // Envoi d'un message aux joueurs
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (isComplete) {
                            player.sendMessage("La zone est complète !");
                        } else {
                            player.sendMessage("La zone n'est pas encore complète.");
                        }
                    }

                    if (isComplete) {
                        // Arrête la tâche de vérification de la zone
                        this.cancel();

                        // Démarre le timer pour transformer les blocs
                        obsidianTransformTask = new BukkitRunnable() {
                            @Override
                            public void run() {
                                World world = corner1.getWorld();
                                int x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
                                int x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
                                int y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
                                int y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
                                int z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
                                int z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

                                for (int x = x1; x <= x2; x++) {
                                    for (int y = y1; y <= y2; y++) {
                                        for (int z = z1; z <= z2; z++) {
                                            Block block = world.getBlockAt(x, y, z);
                                            block.setType(Material.OBSIDIAN);
                                        }
                                    }
                                }

                                // Envoie un message aux joueurs après la transformation
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendMessage("La transformation de la zone en obsidienne est terminée !");
                                }
                            }
                        }.runTaskLater(plugin, 200L); // 200 ticks = 10 secondes
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L); // Vérifie toutes les secondes
        }
    }

    public void stopGame() {
        if (zoneCheckTask != null) {
            zoneCheckTask.cancel();
        }
        if (obsidianTransformTask != null) {
            obsidianTransformTask.cancel();
        }
        gameRunning = false;
    }
}
