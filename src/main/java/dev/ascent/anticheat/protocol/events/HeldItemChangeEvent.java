package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class HeldItemChangeEvent extends Event {
    private final Player player;
    private final int slot;
    private final Object rawData;

    public HeldItemChangeEvent(Player player, int slot, Object rawData) {
        this.player = player; this.slot = slot; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getSlot() { return slot; }
    public Object getRawData() { return rawData; }
}