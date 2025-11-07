package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class WindowItemsEvent extends Event {
    private final Player player;
    private final int windowId;
    private final int itemCount;
    private final Object rawData;

    public WindowItemsEvent(Player player, int windowId, int itemCount, Object rawData) {
        this.player = player; this.windowId = windowId; this.itemCount = itemCount; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getWindowId() { return windowId; }
    public int getItemCount() { return itemCount; }
    public Object getRawData() { return rawData; }
}