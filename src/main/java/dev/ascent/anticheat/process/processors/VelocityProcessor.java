package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.VelocityEvent;
import dev.ascent.anticheat.user.User;

@ProcessorInfo(name = "Velocity")
public final class VelocityProcessor extends Processor {

    private ListenerHandle handle;

    private int lastEntityId;
    private int vx, vy, vz; // raw 1/8000 units like beta protocol
    private long lastVelocityMs;

    public VelocityProcessor(User user) { super(user); }

    @Override
    public void register() {
        handle = ChecksBus.on(VelocityEvent.class, e -> {
            this.lastEntityId = e.getEntityId();
            this.vx = e.getMotionX();
            this.vy = e.getMotionY();
            this.vz = e.getMotionZ();
            this.lastVelocityMs = System.currentTimeMillis();
        });
    }

    @Override
    public void unregister() {
        if (handle != null) { handle.unsubscribe(); handle = null; }
    }

    public int getLastEntityId() { return lastEntityId; }
    public int getVx() { return vx; }
    public int getVy() { return vy; }
    public int getVz() { return vz; }
    public long getLastVelocityMs() { return lastVelocityMs; }
}