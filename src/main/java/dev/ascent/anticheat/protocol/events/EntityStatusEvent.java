package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class EntityStatusEvent extends Event {
    private final Player player;
    private final int entityId;
    private final int status;
    private final Object rawData;

    public EntityStatusEvent(Player player, int entityId, int status, Object rawData) {
        this.player = player;
        this.entityId = entityId;
        this.status = status;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getEntityId() { return entityId; }
    public int getStatus() { return status; }
    public Object getRawData() { return rawData; }
}