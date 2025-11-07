package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class PositionUpdateEvent extends Event {
    public enum Kind { CLIENT_POSITION, CLIENT_LOOK, CLIENT_FLYING, SERVER_POS, SERVER_LOOK }

    private final Player player;
    private final double x, y, z;
    private final float yaw, pitch;
    private final boolean onGround;
    private final Kind kind;

    public PositionUpdateEvent(Player player, double x, double y, double z, float yaw, float pitch, boolean onGround, Kind kind) {
        this.player = player;
        this.x = x; this.y = y; this.z = z;
        this.yaw = yaw; this.pitch = pitch;
        this.onGround = onGround;
        this.kind = kind;
    }

    public Player getPlayer() { return player; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public boolean isOnGround() { return onGround; }
    public Kind getKind() { return kind; }
}