package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class HandshakeEvent extends Event {
    private final Player player;
    private final Object rawData;

    public HandshakeEvent(Player player, Object rawData) {
        this.player = player; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public Object getRawData() { return rawData; }
}