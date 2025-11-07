package dev.ascent.anticheat.process.processors;

import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.process.Processor;
import dev.ascent.anticheat.process.ProcessorInfo;
import dev.ascent.anticheat.protocol.events.UseEntityEvent;
import dev.ascent.anticheat.user.User;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;

@ProcessorInfo(name = "Interaction")
public final class InteractionProcessor extends Processor {

    private ListenerHandle handle;

    private int  lastTargetId;
    private boolean lastWasAttack;
    private long lastInteractMs;
    private long lastAttackMs;

    // --- CPS tracking over a rolling 5s window ---
    private final Deque<Long> recentClicks = new ArrayDeque<Long>(); // timestamps (ms) of attacks in last 5s
    private long   sessionClickCount;
    private double cpsMin = Double.POSITIVE_INFINITY;
    private double cpsMax = 0.0;
    private double cpsSum;
    private long   cpsSamples;

    public InteractionProcessor(User user) { super(user); }

    @Override
    public void register() {
        handle = ChecksBus.on(UseEntityEvent.class, e -> {
            lastTargetId   = e.getTargetEntityId();
            lastWasAttack  = resolveAttackFlag(e);
            lastInteractMs = System.currentTimeMillis();
            if (lastWasAttack) {
                lastAttackMs = lastInteractMs;
                // record click
                recentClicks.addLast(lastAttackMs);
                sessionClickCount++;

                // prune older than 5s
                final long cutoff = lastAttackMs - 5000L;
                while (!recentClicks.isEmpty() && recentClicks.peekFirst() < cutoff) {
                    recentClicks.removeFirst();
                }

                // snapshot CPS (clicks per second over 5s window)
                double cpsNow = recentClicks.size() / 5.0D;
                cpsMin = Math.min(cpsMin, cpsNow);
                cpsMax = Math.max(cpsMax, cpsNow);
                cpsSum += cpsNow;
                cpsSamples++;
            }
        });
    }

    @Override
    public void unregister() {
        if (handle != null) { handle.unsubscribe(); handle = null; }
    }

    /** Support either isAttack() or isLeftClick() depending on your event version. */
    private static boolean resolveAttackFlag(UseEntityEvent e) {
        try {
            Method m = e.getClass().getMethod("isAttack");
            Object o = m.invoke(e);
            if (o instanceof Boolean) return ((Boolean) o).booleanValue();
        } catch (Throwable ignored) {}
        try {
            Method m = e.getClass().getMethod("isLeftClick");
            Object o = m.invoke(e);
            if (o instanceof Boolean) return ((Boolean) o).booleanValue();
        } catch (Throwable ignored) {}
        return false;
    }

    // --- getters ---

    public int getLastTargetId()        { return lastTargetId; }
    public boolean isLastWasAttack()    { return lastWasAttack; }
    public long getLastInteractMillis() { return lastInteractMs; }
    public long getLastAttackMillis()   { return lastAttackMs; }

    /** CPS in the last 5s rolling window (computed live). */
    public double getCpsNow() {
        final long now = System.currentTimeMillis();
        final long cutoff = now - 5000L;
        while (!recentClicks.isEmpty() && recentClicks.peekFirst() < cutoff) {
            recentClicks.removeFirst();
        }
        return recentClicks.size() / 5.0D;
    }

    /** Average CPS across session (based on window snapshots at each attack). */
    public double getCpsAvg() { return cpsSamples == 0 ? 0.0D : (cpsSum / cpsSamples); }
    public double getCpsMin() { return cpsSamples == 0 ? 0.0D : cpsMin; }
    public double getCpsMax() { return cpsMax; }
    public long   getSessionClickCount() { return sessionClickCount; }
}
