package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class EntityActionEvent extends Event {
    private final Player player;
    private final int action;
    private final int auxData;
    private final Object rawData;

    public EntityActionEvent(Player player, int action, int auxData, Object rawData) {
        this.player = player; this.action = action; this.auxData = auxData; this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public int getAction() { return action; }
    public int getAuxData() { return auxData; }
    public Object getRawData() { return rawData; }
}