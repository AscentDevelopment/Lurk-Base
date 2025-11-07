package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class MultiBlockChangeEvent extends Event {
    private final Player player;
    private final int chunkX, chunkZ;
    private final int changeCount;
    private final Object rawData;

    public MultiBlockChangeEvent(Player player, int chunkX, int chunkZ, int changeCount, Object rawData) {
        this.player = player;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.changeCount = changeCount;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getChunkX() { return chunkX; }
    public int getChunkZ() { return chunkZ; }
    public int getChangeCount() { return changeCount; }
    public Object getRawData() { return rawData; }
}