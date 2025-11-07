package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

/** Server relative look update for an entity (yaw/pitch only), e.g., Packet32EntityLook. */
public final class EntityLookEvent extends Event {
    private final Player player;
    private final int entityId;
    private final float yaw, pitch;
    private final Object rawData;

    public EntityLookEvent(Player player, int entityId, float yaw, float pitch, Object rawData) {
        this.player = player;
        this.entityId = entityId;
        this.yaw = yaw; this.pitch = pitch;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public Object getRawData() { return rawData; }
}