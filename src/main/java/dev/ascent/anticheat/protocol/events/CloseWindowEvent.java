package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class CloseWindowEvent extends Event {
    private final Player player;
    private final int windowId;
    private final Object rawData;

    public CloseWindowEvent(Player player, int windowId, Object rawData) {
        this.player = player; this.windowId = windowId; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getWindowId() { return windowId; }
    public Object getRawData() { return rawData; }
}