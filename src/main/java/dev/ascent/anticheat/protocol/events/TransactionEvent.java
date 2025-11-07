package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class TransactionEvent extends Event {
    private final Player player;
    private final int windowId;
    private final short actionNumber;
    private final boolean accepted;
    private final Object rawData;

    public TransactionEvent(Player player, int windowId, short actionNumber, boolean accepted, Object rawData) {
        this.player = player;
        this.windowId = windowId;
        this.actionNumber = actionNumber;
        this.accepted = accepted;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getWindowId() { return windowId; }
    public short getActionNumber() { return actionNumber; }
    public boolean isAccepted() { return accepted; }
    public Object getRawData() { return rawData; }
}