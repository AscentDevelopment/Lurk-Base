package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.PositionUpdateEvent;
import dev.ascent.anticheat.user.User;

@ProcessorInfo(name = "Teleport")
public final class TeleportProcessor extends Processor {

    private ListenerHandle handle;

    private boolean recentTeleport;
    private long lastTeleportMs;
    private double expectX, expectY, expectZ;
    private float expectYaw, expectPitch;

    public TeleportProcessor(User user) { super(user); }

    @Override
    public void register() {
        handle = ChecksBus.on(PositionUpdateEvent.class, e -> {
            String k = e.getKind().name();
            if (k.equals("SERVER_POSLOOK") || k.equals("SERVER_POSITION_LOOK")) {
                recentTeleport = true;
                lastTeleportMs = System.currentTimeMillis();
                expectX = e.getX(); expectY = e.getY(); expectZ = e.getZ();
                expectYaw = e.getYaw(); expectPitch = e.getPitch();
                return;
            }

            // Any client movement decays the teleport flag after ~500ms
            if (k.equals("POSITION") || k.equals("CLIENT_POSITION")
             || k.equals("LOOK") || k.equals("CLIENT_LOOK")
             || k.equals("POSITION_LOOK") || k.equals("CLIENT_POSITION_LOOK")) {
                if (recentTeleport && System.currentTimeMillis() - lastTeleportMs > 500L) {
                    recentTeleport = false;
                }
            }
        });
    }

    @Override
    public void unregister() {
        if (handle != null) { handle.unsubscribe(); handle = null; }
    }

    public boolean isRecentTeleport() { return recentTeleport; }
    public long getLastTeleportMs() { return lastTeleportMs; }
    public double getExpectX() { return expectX; }
    public double getExpectY() { return expectY; }
    public double getExpectZ() { return expectZ; }
    public float getExpectYaw() { return expectYaw; }
    public float getExpectPitch() { return expectPitch; }
}