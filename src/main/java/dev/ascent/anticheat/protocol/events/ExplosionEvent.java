package dev.ascent.anticheat.protocol.events;

import org.bukkit.entity.Player;

public final class ExplosionEvent extends Event {
    private final Player player;
    private final double x, y, z;
    private final float strength;
    private final Object rawData;

    public ExplosionEvent(Player player, double x, double y, double z, float strength, Object rawData) {
        this.player = player;
        this.x = x; this.y = y; this.z = z;
        this.strength = strength;
        this.rawData = rawData;
    }

    public Player getPlayer() { return player; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getStrength() { return strength; }
    public Object getRawData() { return rawData; }
}