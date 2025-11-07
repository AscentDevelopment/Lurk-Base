package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class DestroyEntityEvent extends Event {
    private final Player player;
    private final int[] entityIds;
    private final Object rawData;

    public DestroyEntityEvent(Player player, int[] entityIds, Object rawData) {
        this.player = player; this.entityIds = entityIds; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int[] getEntityIds() { return entityIds; }
    public Object getRawData() { return rawData; }
}