package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class BlockChangeEvent extends Event {
    private final Player player;
    private final int x, y, z;
    private final int blockId;
    private final int blockData;
    private final Object rawData;

    public BlockChangeEvent(Player player, int x, int y, int z, int blockId, int blockData, Object rawData) {
        this.player = player;
        this.x = x; this.y = y; this.z = z;
        this.blockId = blockId;
        this.blockData = blockData;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getBlockId() { return blockId; }
    public int getBlockData() { return blockData; }
    public Object getRawData() { return rawData; }
}