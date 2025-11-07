package dev.ascent.anticheat.packet;

import dev.ascent.anticheat.Lurk;
import dev.ascent.anticheat.bus.ChecksBus;
import dev.ascent.anticheat.bus.ChecksBus.ListenerHandle;
import dev.ascent.anticheat.protocol.events.BlockDigEvent;
import dev.ascent.anticheat.protocol.events.PositionUpdateEvent;
import dev.ascent.anticheat.protocol.events.UseEntityEvent;
import dev.ascent.anticheat.protocol.events.VelocityEvent;
import dev.ascent.anticheat.user.User;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Beta 1.7.3 PacketManager:
 * Bridges protocol events (posted on ChecksBus) to per-user checks/processors.
 * Uses reflection so you can compile/run even if checks/processors aren't restored yet.
 */
public final class PacketManager {

    private ListenerHandle listenerHandle;

    /** Start listening to all packet-related events from the ChecksBus. */
    public void start() {
        listenerHandle = ChecksBus.on(Object.class, evt -> {
            // Extract player from known protocol event types
            Player player = null;
            if (evt instanceof PositionUpdateEvent) player = ((PositionUpdateEvent) evt).getPlayer();
            else if (evt instanceof VelocityEvent)   player = ((VelocityEvent) evt).getPlayer();
            else if (evt instanceof BlockDigEvent)   player = ((BlockDigEvent) evt).getPlayer();
            else if (evt instanceof UseEntityEvent)  player = ((UseEntityEvent) evt).getPlayer();

            if (player == null) return;

            User user = Lurk.getInstance().getUserManager().getUser(player);
            if (user == null) return;

            // ---- Checks (if User#getChecks() exists) ----
            Collection<?> checks = getUserChecks(user);
            if (checks != null) {
                for (Object check : checks) {
                    if (check == null) continue;
                    if (!isEnabled(check)) continue;        // skip if check#isEnabled() exists and is false
                    fanout(check, evt);                     // invoke onPosition/onVelocity/etc if present
                }
            }

            // ---- Processors (if User#getProcessorManager().getProcessors() exist) ----
            Iterable<?> processors = getUserProcessors(user);
            if (processors != null) {
                for (Object proc : processors) {
                    if (proc == null) continue;
                    fanout(proc, evt);
                }
            }
        });
    }

    /** Stop listening and clean up the bus registration. */
    public void stop() {
        if (listenerHandle != null) {
            listenerHandle.unsubscribe();
            listenerHandle = null;
        }
    }

    // ---------- Reflection helpers (compile-safe while you port back code) ----------

    @SuppressWarnings("unchecked")
    private static Collection<?> getUserChecks(User user) {
        try {
            Method m = user.getClass().getMethod("getChecks");
            Object o = m.invoke(user);
            if (o instanceof Collection) return (Collection<?>) o;
        } catch (Throwable ignored) { }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Iterable<?> getUserProcessors(User user) {
        try {
            Method pm = user.getClass().getMethod("getProcessorManager");
            Object procMgr = pm.invoke(user);
            if (procMgr == null) return null;
            Method getProcs = procMgr.getClass().getMethod("getProcessors");
            Object list = getProcs.invoke(procMgr);
            if (list instanceof Iterable) return (Iterable<?>) list;
        } catch (Throwable ignored) { }
        return null;
    }

    private static boolean isEnabled(Object check) {
        try {
            Method m = check.getClass().getMethod("isEnabled");
            Object o = m.invoke(check);
            return !(o instanceof Boolean) || (Boolean) o;
        } catch (Throwable ignored) {
            // If method doesn't exist, assume enabled.
            return true;
        }
    }

    /** Call target.onXxx(EventType) if such a method exists and evt matches the parameter type. */
    private static void fanout(Object target, Object evt) {
        invokeIfPresent(target, "onPosition", PositionUpdateEvent.class, evt);
        invokeIfPresent(target, "onVelocity",  VelocityEvent.class,       evt);
        invokeIfPresent(target, "onBlockDig",  BlockDigEvent.class,       evt);
        invokeIfPresent(target, "onUseEntity", UseEntityEvent.class,      evt);
    }

    private static void invokeIfPresent(Object target, String methodName, Class<?> paramType, Object evt) {
        if (!paramType.isInstance(evt)) return;
        try {
            Method m = target.getClass().getMethod(methodName, paramType);
            m.invoke(target, paramType.cast(evt));
        } catch (NoSuchMethodException ignored) {
            // target doesn't handle this event type; that's fine.
        } catch (Throwable ignored) {
            // Swallow to keep the bus resilient; add logging if desired.
        }
    }
}