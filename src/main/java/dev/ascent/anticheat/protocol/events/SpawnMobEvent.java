package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class SpawnMobEvent extends Event {
    private final Player player;
    private final int entityId;
    private final int mobType;
    private final Object rawData;

    public SpawnMobEvent(Player player, int entityId, int mobType, Object rawData) {
        this.player = player; this.entityId = entityId; this.mobType = mobType; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public int getMobType() { return mobType; }
    public Object getRawData() { return rawData; }
}