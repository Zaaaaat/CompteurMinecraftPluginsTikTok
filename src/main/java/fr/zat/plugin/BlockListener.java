package fr.zat.plugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBurnEvent;

public class BlockListener implements Listener {
    private final ZoneManager zoneManager;

    public BlockListener(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        if (zoneManager.isBlockInZone(brokenBlock.getLocation())) {
            zoneManager.onBlockBrokenInZone();
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (zoneManager.isBlockInZone(block.getLocation())) {
            zoneManager.onBlockBrokenInZone();
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (zoneManager.isBlockInZone(block.getLocation()) && block.getType() != Material.OBSIDIAN) {
            zoneManager.onBlockBrokenInZone();
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        if (zoneManager.isBlockInZone(placedBlock.getLocation())) {
            // Ne pas appeler onBlockBrokenInZone si le bloc placé est de l'air
            if (placedBlock.getType() != Material.AIR) {
                zoneManager.onBlockBrokenInZone();
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (zoneManager.isBlockInZone(block.getLocation())) {
            zoneManager.onBlockBrokenInZone();
        }
    }
}
