package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.PositionUpdateEvent;
import dev.ascent.anticheat.user.User;

@ProcessorInfo(name = "Rotation")
public final class RotationProcessor extends Processor {

    private ListenerHandle handle;

    private float lastYaw, lastPitch;
    private float dyaw, dpitch;
    private float prevDyaw, prevDpitch;
    private float yawAccel, pitchAccel;
    private long lastLookMs;

    public RotationProcessor(User user) { super(user); }

    @Override
    public void register() {
        handle = ChecksBus.on(PositionUpdateEvent.class, e -> {
            String k = e.getKind().name();
            if (k.equals("LOOK") || k.equals("CLIENT_LOOK")
             || k.equals("POSITION_LOOK") || k.equals("CLIENT_POSITION_LOOK")) {
                onLook(e);
            }
        });
    }

    @Override
    public void unregister() {
        if (handle != null) { handle.unsubscribe(); handle = null; }
    }

    private void onLook(PositionUpdateEvent e) {
        float yaw = e.getYaw();
        float pitch = e.getPitch();

        prevDyaw = dyaw; prevDpitch = dpitch;
        dyaw = yaw - lastYaw;
        dpitch = pitch - lastPitch;

        yawAccel = dyaw - prevDyaw;
        pitchAccel = dpitch - prevDpitch;

        lastYaw = yaw;
        lastPitch = pitch;
        lastLookMs = System.currentTimeMillis();
    }

    public float getDyaw() { return dyaw; }
    public float getDpitch() { return dpitch; }
    public float getYawAccel() { return yawAccel; }
    public float getPitchAccel() { return pitchAccel; }
    public long getLastLookMs() { return lastLookMs; }
}