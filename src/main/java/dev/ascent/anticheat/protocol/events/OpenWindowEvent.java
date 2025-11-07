package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class OpenWindowEvent extends Event {
    private final Player player;
    private final int windowId;
    private final int typeId;
    private final String title;
    private final int slots;
    private final Object rawData;

    public OpenWindowEvent(Player player, int windowId, int typeId, String title, int slots, Object rawData) {
        this.player = player;
        this.windowId = windowId;
        this.typeId = typeId;
        this.title = title;
        this.slots = slots;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getWindowId() { return windowId; }
    public int getTypeId() { return typeId; }
    public String getTitle() { return title; }
    public int getSlots() { return slots; }
    public Object getRawData() { return rawData; }
}