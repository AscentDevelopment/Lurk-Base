package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class PickupSpawnEvent extends Event {
    private final Player player;
    private final int entityId;
    private final int itemId;
    private final int itemCount;
    private final double x, y, z;
    private final Object rawData;

    public PickupSpawnEvent(Player player, int entityId, int itemId, int itemCount, double x, double y, double z, Object rawData) {
        this.player = player;
        this.entityId = entityId;
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.x = x; this.y = y; this.z = z;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public int getItemId() { return itemId; }
    public int getItemCount() { return itemCount; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public Object getRawData() { return rawData; }
}