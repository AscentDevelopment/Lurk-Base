package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

/** Server → client velocity/knockback. */
public final class VelocityEvent extends Event {
    private final Player player;
    private final int entityId;
    private final int motionX, motionY, motionZ;

    public VelocityEvent(Player player, int entityId, int motionX, int motionY, int motionZ) {
        this.player = player;
        this.entityId = entityId;
        this.motionX = motionX; this.motionY = motionY; this.motionZ = motionZ;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public int getMotionX() { return motionX; }
    public int getMotionY() { return motionY; }
    public int getMotionZ() { return motionZ; }
}