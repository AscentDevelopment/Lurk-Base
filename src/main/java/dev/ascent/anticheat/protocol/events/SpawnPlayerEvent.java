package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class SpawnPlayerEvent extends Event {
    private final Player player;
    private final int entityId;
    private final Object rawData;

    public SpawnPlayerEvent(Player player, int entityId, Object rawData) {
        this.player = player; this.entityId = entityId; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public Object getRawData() { return rawData; }
}