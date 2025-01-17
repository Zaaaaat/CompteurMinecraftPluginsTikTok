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

import java.util.ArrayList;
import java.util.List;

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

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    private void startZoneCheck() {
        zoneCheckTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameRunning) return;

                boolean isComplete = isZoneComplete();
                boolean isAlreadyObsidian = isZoneTransformedToObsidian();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isComplete && !isAlreadyObsidian) {
                        player.sendMessage("La zone est complète ! Commencement du compte à rebours.");
                        startCountdown();
                    } else if (!isComplete) {
                        player.sendMessage("La zone n'est pas encore complète.");
                    }
                }

                if (isComplete && !isAlreadyObsidian) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private boolean isZoneComplete() {
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
                    if (block.getType().isAir()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
                if (isZoneTransformedToObsidian()) {
                    this.cancel();
                    countdownRunning = false;
                    return;
                }

                if (timeLeft <= 0) {
                    this.cancel();
                    countdownRunning = false;
                    transformToObsidian();
                } else {
                    displayCountdownMessage(timeLeft);
                    timeLeft--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void displayCountdownMessage(int timeLeft) {
        TextComponent actionBarMessage = new TextComponent("CHECKPOINT DANS : " + timeLeft + " SECONDES.");
        actionBarMessage.setColor(timeLeft <= 3 ? net.md_5.bungee.api.ChatColor.RED : net.md_5.bungee.api.ChatColor.YELLOW);
        actionBarMessage.setBold(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
        }
    }

    private boolean isZoneTransformedToObsidian() {
        for (Block block : getBlocksInZone()) {
            if (block.getType() != Material.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }

    public List<Block> getBlocksInZone() {
        List<Block> blocks = new ArrayList<>();
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
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public void onBlockBrokenInZone() {
        if (countdownRunning) {
            countdownTask.cancel();
            countdownRunning = false;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Un bloc a été cassé dans la zone. Le compte à rebours est annulé !");
            }
            startZoneCheck();
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
        for (Block block : getBlocksInZone()) {
            block.setType(Material.OBSIDIAN);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            player.sendMessage("La transformation de la zone en obsidienne est terminée !");
        }

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
