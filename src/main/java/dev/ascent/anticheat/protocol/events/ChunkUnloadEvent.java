package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class ChunkUnloadEvent extends Event {
    private final Player player;
    private final int chunkX, chunkZ;
    private final Object rawData;

    public ChunkUnloadEvent(Player player, int chunkX, int chunkZ, Object rawData) {
        this.player = player;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getChunkX() { return chunkX; }
    public int getChunkZ() { return chunkZ; }
    public Object getRawData() { return rawData; }
}