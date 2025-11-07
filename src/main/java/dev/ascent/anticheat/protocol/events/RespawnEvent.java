package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class RespawnEvent extends Event {
    private final Player player;
    private final int dimension;
    private final Object rawData;

    public RespawnEvent(Player player, int dimension, Object rawData) {
        this.player = player;
        this.dimension = dimension;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getDimension() { return dimension; }
    public Object getRawData() { return rawData; }
}