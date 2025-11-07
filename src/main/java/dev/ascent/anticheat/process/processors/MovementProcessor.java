package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.PositionUpdateEvent;
import dev.ascent.anticheat.user.User;
import dev.ascent.anticheat.util.location.FlyingLocation;
import org.bukkit.World;

@ProcessorInfo(name = "Movement")
public final class MovementProcessor extends Processor {

    private ListenerHandle handle;

    private final FlyingLocation to       = new FlyingLocation();
    private final FlyingLocation from     = new FlyingLocation();
    private final FlyingLocation fromFrom = new FlyingLocation();

    private double deltaX, deltaY, deltaZ, deltaXZ;
    private double deltaXAbs, deltaYAbs, deltaZAbs, lastDeltaXZ;
    private double lastDeltaX, lastDeltaY, lastDeltaZ;
    private float  deltaYaw, deltaPitch, deltaYawAbs, deltaPitchAbs;
    private float  lastDeltaYaw, lastDeltaPitch, lastDeltaYawAbs, lastDeltaPitchAbs;

    private int tick;

    public MovementProcessor(User user) { super(user); }

    @Override
    public void register() {
        handle = ChecksBus.on(Object.class, evt -> {
            if (evt instanceof PositionUpdateEvent) onPosition((PositionUpdateEvent) evt);
        });
    }

    @Override
    public void unregister() {
        if (handle != null) { handle.unsubscribe(); handle = null; }
    }

    private static boolean isPosKind(String k) {
        // tolerate both our enum and any client/server-prefixed variants you add later
        return "POSITION".equals(k) || "CLIENT_POSITION".equals(k) ||
               "POSITION_LOOK".equals(k) || "CLIENT_POSITION_LOOK".equals(k);
    }

    private static boolean isRotKind(String k) {
        return "LOOK".equals(k) || "CLIENT_LOOK".equals(k) ||
               "POSITION_LOOK".equals(k) || "CLIENT_POSITION_LOOK".equals(k);
    }

    private void onPosition(PositionUpdateEvent e) {
        final String k = e.getKind().name();
        final boolean pos = isPosKind(k);
        final boolean rot = isRotKind(k);
        if (!pos && !rot) return;
        applyUpdate(e, pos, rot);
    }

    private void applyUpdate(PositionUpdateEvent e, boolean pos, boolean rot) {
        World worldNow = getData().getPlayer().getWorld();

        // shift worlds
        fromFrom.setWorld(from.getWorld());
        from.setWorld(to.getWorld());
        to.setWorld(worldNow);

        // onGround
        fromFrom.setOnGround(from.isOnGround());
        from.setOnGround(to.isOnGround());
        to.setOnGround(e.isOnGround());

        // ticks
        fromFrom.setTick(from.getTick());
        from.setTick(to.getTick());
        to.setTick(tick);

        // --- position pipeline ---
        if (pos) {
            // shift positions
            fromFrom.setPosX(from.getPosX());
            fromFrom.setPosY(from.getPosY());
            fromFrom.setPosZ(from.getPosZ());

            from.setPosX(to.getPosX());
            from.setPosY(to.getPosY());
            from.setPosZ(to.getPosZ());

            // apply new
            if (!Double.isNaN(e.getX())) to.setPosX(e.getX());
            if (!Double.isNaN(e.getY())) to.setPosY(e.getY());
            if (!Double.isNaN(e.getZ())) to.setPosZ(e.getZ());

            // deltas
            lastDeltaX = deltaX; lastDeltaY = deltaY; lastDeltaZ = deltaZ;

            deltaX = to.getPosX() - from.getPosX();
            deltaY = to.getPosY() - from.getPosY();
            deltaZ = to.getPosZ() - from.getPosZ();

            deltaXAbs = Math.abs(deltaX);
            deltaYAbs = Math.abs(deltaY);
            deltaZAbs = Math.abs(deltaZ);

            lastDeltaXZ = deltaXZ;
            deltaXZ = Math.hypot(deltaXAbs, deltaZAbs);

            // clamp tiny noise
            if (deltaXZ < 1.0E-6) deltaXZ = 0.0;
            if (Math.abs(deltaY) < 1.0E-6) deltaY = 0.0;
        }

        // --- rotation pipeline ---
        if (rot) {
            fromFrom.setYaw(from.getYaw());
            fromFrom.setPitch(from.getPitch());

            from.setYaw(to.getYaw());
            from.setPitch(to.getPitch());

            if (!Float.isNaN(e.getYaw()))   to.setYaw(e.getYaw());
            if (!Float.isNaN(e.getPitch())) to.setPitch(e.getPitch());

            lastDeltaYaw = deltaYaw;
            lastDeltaPitch = deltaPitch;

            deltaYaw = to.getYaw() - from.getYaw();
            deltaPitch = to.getPitch() - from.getPitch();

            lastDeltaYawAbs = deltaYawAbs;
            lastDeltaPitchAbs = deltaPitchAbs;

            deltaYawAbs = Math.abs(deltaYaw);
            deltaPitchAbs = Math.abs(deltaPitch);
        }

        tick++;
    }

    // Convenience helpers for checks (keeps your check code clean)
    public boolean isOnGround()    { return to.isOnGround(); }
    public boolean wasOnGround()   { return from.isOnGround(); }
    public boolean wasWasGround()  { return fromFrom.isOnGround(); }

    // getters for checks
    public FlyingLocation getTo() { return to; }
    public FlyingLocation getFrom() { return from; }
    public FlyingLocation getFromFrom() { return fromFrom; }
    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
    public double getDeltaZ() { return deltaZ; }
    public double getDeltaXZ() { return deltaXZ; }
    public double getDeltaXAbs() { return deltaXAbs; }
    public double getDeltaYAbs() { return deltaYAbs; }
    public double getDeltaZAbs() { return deltaZAbs; }
    public double getLastDeltaXZ() { return lastDeltaXZ; }
    public double getLastDeltaX() { return lastDeltaX; }
    public double getLastDeltaY() { return lastDeltaY; }
    public double getLastDeltaZ() { return lastDeltaZ; }
    public float getDeltaYaw() { return deltaYaw; }
    public float getDeltaPitch() { return deltaPitch; }
    public float getDeltaYawAbs() { return deltaYawAbs; }
    public float getDeltaPitchAbs() { return deltaPitchAbs; }
    public float getLastDeltaYaw() { return lastDeltaYaw; }
    public float getLastDeltaPitch() { return lastDeltaPitch; }
    public float getLastDeltaYawAbs() { return lastDeltaYawAbs; }
    public float getLastDeltaPitchAbs() { return lastDeltaPitchAbs; }
    public int getTick() { return tick; }
}