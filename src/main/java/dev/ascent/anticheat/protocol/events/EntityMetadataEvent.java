package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class EntityMetadataEvent extends Event {
    private final Player player;
    private final int entityId;
    private final Object metadata; // keep raw; format varies by fork
    private final Object rawData;

    public EntityMetadataEvent(Player player, int entityId, Object metadata, Object rawData) {
        this.player = player;
        this.entityId = entityId;
        this.metadata = metadata;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public Object getMetadata() { return metadata; }
    public Object getRawData() { return rawData; }
}