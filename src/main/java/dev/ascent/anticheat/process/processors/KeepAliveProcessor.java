package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.KeepAliveEvent;
import dev.ascent.anticheat.user.User;

import java.util.concurrent.atomic.AtomicLong;

@ProcessorInfo(name = "KeepAlive")
public final class KeepAliveProcessor extends Processor {

    private ListenerHandle handle;

    private final AtomicLong lastKeepAliveId = new AtomicLong(0L);
    private volatile long lastKeepAliveMs;
    private volatile long lastRttMs; // if you later correlate IDs with replies

    public KeepAliveProcessor(User user) { super(user); }

    @Override
    public void register() {
        handle = ChecksBus.on(KeepAliveEvent.class, e -> {
            lastKeepAliveId.set(e.getId());
            lastKeepAliveMs = System.currentTimeMillis();
            // If you wire server→client keepalive IDs later, compute RTT from sent→recv.
        });
    }

    @Override
    public void unregister() {
        if (handle != null) { handle.unsubscribe(); handle = null; }
    }

    public long getLastKeepAliveId() { return lastKeepAliveId.get(); }
    public long getLastKeepAliveMs() { return lastKeepAliveMs; }
    public long getLastRttMs() { return lastRttMs; }
    public void setLastRttMs(long v) { this.lastRttMs = v; }
}