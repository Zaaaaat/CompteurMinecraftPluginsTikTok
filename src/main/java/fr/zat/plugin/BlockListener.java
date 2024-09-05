package fr.zat.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.Block;

public class BlockListener implements Listener {
    private final ZoneManager zoneManager;

    public BlockListener(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();

        // Vérifie si le bloc cassé est dans la zone définie
        if (zoneManager.isBlockInZone(brokenBlock.getLocation())) {
            zoneManager.onBlockBrokenInZone();
        }
    }
}
