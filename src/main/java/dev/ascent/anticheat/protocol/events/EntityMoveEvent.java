package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

/** Server relative move for an entity (no yaw/pitch), e.g., Packet31RelEntityMove. */
public final class EntityMoveEvent extends Event {
    private final Player player;
    private final int entityId;
    private final double dx, dy, dz;
    private final Object rawData;

    public EntityMoveEvent(Player player, int entityId, double dx, double dy, double dz, Object rawData) {
        this.player = player;
        this.entityId = entityId;
        this.dx = dx; this.dy = dy; this.dz = dz;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public double getDz() { return dz; }
    public Object getRawData() { return rawData; }
}