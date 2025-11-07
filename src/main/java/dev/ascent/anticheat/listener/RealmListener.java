package dev.ascent.anticheat.listener;

import dev.ascent.anticheat.bus.ChecksBus;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

/** Forwards world/chunk events to the bus. */
public final class RealmListener extends WorldListener {

    @Override
    public void onChunkLoad(ChunkLoadEvent event) {
        ChecksBus.post(event);
    }

    @Override
    public void onChunkUnload(ChunkUnloadEvent event) {
        ChecksBus.post(event);
    }
}