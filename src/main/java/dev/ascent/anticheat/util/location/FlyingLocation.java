package dev.ascent.anticheat.util.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/** Beta-safe movement snapshot for CB 1060. */
public class FlyingLocation {
    private int tick;
    private double posX, posY, posZ;
    private float yaw, pitch;
    private boolean onGround;
    private World world;

    public FlyingLocation() {}

    public FlyingLocation(Location loc, int tick) {
        this.tick = tick;
        this.posX = loc.getX();
        this.posY = loc.getY();
        this.posZ = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.onGround = false;
        this.world = loc.getWorld();
    }

    public int getTick() { return tick; }
    public void setTick(int tick) { this.tick = tick; }
    public double getPosX() { return posX; }
    public void setPosX(double posX) { this.posX = posX; }
    public double getPosY() { return posY; }
    public void setPosY(double posY) { this.posY = posY; }
    public double getPosZ() { return posZ; }
    public void setPosZ(double posZ) { this.posZ = posZ; }
    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public World getWorld() { return world; }
    public void setWorld(World world) { this.world = world; }

    public Location toBukkit() {
        return new Location(world, posX, posY, posZ, yaw, pitch);
    }

    public Vector toVector() {
        return new Vector(posX, posY, posZ);
    }

    public double distanceSquaredXZ(FlyingLocation o) {
        if (o.getWorld() != null && this.world != null && o.getWorld() == this.world) {
            return square(this.posX - o.posX) + square(this.posZ - o.posZ);
        }
        return 0.0;
    }

    private static double square(double d) { return d * d; }
}