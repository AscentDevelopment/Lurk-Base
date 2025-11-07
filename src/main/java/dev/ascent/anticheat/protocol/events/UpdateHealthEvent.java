package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class UpdateHealthEvent extends Event {
    private final Player player;
    private final float health;
    private final Object rawData;

    public UpdateHealthEvent(Player player, float health, Object rawData) {
        this.player = player;
        this.health = health;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public float getHealth() { return health; }
    public Object getRawData() { return rawData; }
}