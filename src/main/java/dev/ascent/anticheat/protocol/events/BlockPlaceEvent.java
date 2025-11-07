package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class BlockPlaceEvent extends Event {
    private final Player player;
    private final int x, y, z;
    private final int face;
    private final Object rawData;

    public BlockPlaceEvent(Player player, int x, int y, int z, int face, Object rawData) {
        this.player = player; this.x = x; this.y = y; this.z = z;
        this.face = face; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getFace() { return face; }
    public Object getRawData() { return rawData; }
}