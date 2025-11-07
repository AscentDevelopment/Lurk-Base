package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class EntityTeleportEvent extends Event {
    private final Player player;
    private final int entityId;
    private final double x, y, z;
    private final float yaw, pitch;
    private final Object rawData;

    public EntityTeleportEvent(Player player, int entityId, double x, double y, double z,
                               float yaw, float pitch, Object rawData) {
        this.player = player; this.entityId = entityId;
        this.x = x; this.y = y; this.z = z;
        this.yaw = yaw; this.pitch = pitch; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public Object getRawData() { return rawData; }
}