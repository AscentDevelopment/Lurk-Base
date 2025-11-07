package dev.ascent.anticheat.listener;

import dev.ascent.anticheat.bus.ChecksBus;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/** Forwards block break/place actions to the bus. */
public final class MaterialListener extends BlockListener {

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        ChecksBus.post(event);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        ChecksBus.post(event);
    }
}