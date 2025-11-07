package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class WindowClickEvent extends Event {
    private final Player player;
    private final int windowId;
    private final int slot;
    private final Object rawData;

    public WindowClickEvent(Player player, int windowId, int slot, Object rawData) {
        this.player = player; this.windowId = windowId; this.slot = slot; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getWindowId() { return windowId; }
    public int getSlot() { return slot; }
    public Object getRawData() { return rawData; }
}