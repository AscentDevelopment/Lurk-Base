package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.BlockDigEvent;
import dev.ascent.anticheat.user.User;

@ProcessorInfo(name = "Dig")
public final class DigProcessor extends Processor {

    private ListenerHandle handle;

    private int lastX, lastY, lastZ, lastFace, lastStatus;
    private long lastDigMs;

    public DigProcessor(User user) { super(user); }

    @Override
    public void register() {
        handle = ChecksBus.on(BlockDigEvent.class, e -> {
            lastX = e.getX();
            lastY = e.getY();
            lastZ = e.getZ();
            lastFace = e.getFace();
            lastStatus = e.getStatus();
            lastDigMs = System.currentTimeMillis();
        });
    }

    @Override
    public void unregister() {
        if (handle != null) { handle.unsubscribe(); handle = null; }
    }

    public int getLastX() { return lastX; }
    public int getLastY() { return lastY; }
    public int getLastZ() { return lastZ; }
    public int getLastFace() { return lastFace; }
    public int getLastStatus() { return lastStatus; }
    public long getLastDigMs() { return lastDigMs; }
}