package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class UseEntityEvent extends Event {
    private final Player player;
    private final int targetEntityId;
    private final boolean leftClick; // attack vs interact

    public UseEntityEvent(Player player, int targetEntityId, boolean leftClick) {
        this.player = player;
        this.targetEntityId = targetEntityId;
        this.leftClick = leftClick;
    }

    public Player getPlayer() { return player; }
    public int getTargetEntityId() { return targetEntityId; }
    public boolean isLeftClick() { return leftClick; }
}