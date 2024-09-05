package fr.zat.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ZoneManager {
    private final Main plugin;
    private Location corner1;
    private Location corner2;
    private boolean gameRunning = false;
    private boolean countdownRunning = false;
    private BukkitTask zoneCheckTask;
    private BukkitTask countdownTask;

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
            startZoneCheck();
        }
    }

    private void startZoneCheck() {
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
                        if (!isComplete) break;
                    }
                    if (!isComplete) break;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isComplete) {
                        player.sendMessage("La zone est complète ! Commencement du compte à rebours.");
                        startCountdown();
                    } else {
                        player.sendMessage("La zone n'est pas encore complète.");
                    }
                }

                if (isComplete) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startCountdown() {
        if (countdownRunning) {
            countdownTask.cancel(); // Annule la tâche précédente si elle est encore en cours
        }
        countdownRunning = true;

        final int countdownTime = 10; // Temps en secondes
        countdownTask = new BukkitRunnable() {
            int timeLeft = countdownTime;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    // Arrête le compte à rebours
                    this.cancel();
                    countdownRunning = false;
                    transformToObsidian();
                } else {
                    // Crée un TextComponent avec des styles personnalisés
                    TextComponent actionBarMessage = new TextComponent("CHECKPOINT DANS : " + timeLeft + " SECONDES.");

                    // Change la couleur en fonction du temps restant
                    if (timeLeft <= 3) {
                        actionBarMessage.setColor(net.md_5.bungee.api.ChatColor.RED);
                    } else {
                        actionBarMessage.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                    }

                    actionBarMessage.setBold(true);

                    // Envoie le message aux joueurs dans l'Action Bar
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
                    }
                    timeLeft--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 seconde
    }

    public void onBlockBrokenInZone() {
        // Si un bloc est cassé pendant le compte à rebours, arrête le compte à rebours
        if (countdownRunning) {
            countdownTask.cancel();
            countdownRunning = false;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Un bloc a été cassé dans la zone. Le compte à rebours est annulé !");
            }
            startZoneCheck(); // Recommence la vérification de la zone
        }
    }

    public boolean isBlockInZone(Location loc) {
        if (!isCornersSet()) return false;

        World world = corner1.getWorld();
        if (!world.equals(loc.getWorld())) return false;

        int x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        return loc.getBlockX() >= x1 && loc.getBlockX() <= x2 &&
                loc.getBlockY() >= y1 && loc.getBlockY() <= y2 &&
                loc.getBlockZ() >= z1 && loc.getBlockZ() <= z2;
    }

    private void transformToObsidian() {
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

        // Efface le message de l'Action Bar pour tous les joueurs
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            player.sendMessage("La transformation de la zone en obsidienne est terminée !");
        }

        // Relance la vérification de la zone sans afficher le compte à rebours
        startZoneCheck();
    }

    public void stopGame() {
        if (zoneCheckTask != null) {
            zoneCheckTask.cancel();
        }
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        gameRunning = false;
        countdownRunning = false;
    }
}
