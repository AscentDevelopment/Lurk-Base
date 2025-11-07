package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class KeepAliveEvent extends Event {
    private final Player player;
    private final int id;
    private final Object rawData;

    public KeepAliveEvent(Player player, int id, Object rawData) {
        this.player = player; this.id = id; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getId() { return id; }
    public Object getRawData() { return rawData; }
}