package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class BlockDigEvent extends Event {
    private final Player player;
    private final int x, y, z;
    private final int status;
    private final int face;

    public BlockDigEvent(Player player, int x, int y, int z, int status, int face) {
        this.player = player;
        this.x = x; this.y = y; this.z = z;
        this.status = status;
        this.face = face;
    }

    public Player getPlayer() { return player; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getStatus() { return status; }
    public int getFace() { return face; }
}